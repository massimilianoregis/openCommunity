package org.opencommunity.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import static org.imgscalr.Scalr.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.sun.mail.util.BASE64DecoderStream;


@Component
public class Util {
	@Value("${community.img.root}") private String root;
	@Value("${community.img.url}") private String url;
	
	
	static private Util instance;
	static public Util getInstance()
		{
		return instance;
		}
	
	public Util(){}
	public Util(String root, String url)
		{
		this.root=root;
		this.url=url;
		init();
		}
	@PostConstruct
	public void init()
		{				
		instance=this;
		}
	
	public String saveImage(String img) throws Exception
		{
		return saveImage(img,null);
		}
	
	public InputStream getImage(String img) throws Exception
		{		
	    return new FileInputStream(new File(root,img));
		}
	public InputStream getImage(String img,Integer w,Integer h) throws Exception
		{
		if(w==null && h==null) return new FileInputStream(new File(root,img));
		
		File rootImage = new File(root);
		String imgtype="jpg";
		String newImage = img+(w!=null?w:"_")+"X"+(h!=null?h:"_");
		File imgOutFile = new File(rootImage,newImage+"."+imgtype);
		if(imgOutFile.exists()) return new FileInputStream(imgOutFile);
			
		BufferedImage image = ImageIO.read(new File(root,img));
		Integer nh = image.getHeight();
		Integer nw = image.getWidth();		
		
		if( ((h!=null && nh==h)||h==null) && ((w!=null && nw==w)||w==null))
			{
			ImageIO.write(image, imgtype, imgOutFile);
			return new FileInputStream(imgOutFile);
			}
		float ratio = nw/(float)nh;		
		Mode mode = Mode.FIT_TO_WIDTH;		
		
		Integer w1 = w;
		Integer h1 = (int)(w/ratio);
		
		Integer w2 = (int)(h*ratio);
		Integer h2 = h;
		
		if(w1>=w && h1>=h) mode= Mode.FIT_TO_WIDTH;
		if(w2>=w && h2>=h) mode= Mode.FIT_TO_HEIGHT;	
		
		BufferedImage resize = resize(image,  Method.BALANCED, mode,w!=null?w:0,h!=null?h:0,OP_ANTIALIAS);		
		if(w!=null && h!=null)  resize= crop(resize,(resize.getWidth()-w)/2,(resize.getHeight()-h)/2,w,h);
		
		ImageIO.write(resize, imgtype, imgOutFile);	
		return new FileInputStream(imgOutFile);
		}
	public String saveImage(String img, String name) throws Exception
		{		
		File rootImage = new File(root);		
		if(img.startsWith("data:"))
			{
			int pos = img.indexOf(",");
			String prefix = img.substring(0,pos);
			String data = img.substring(pos+1);			
			String imgtype=prefix.substring(prefix.indexOf("/")+1,prefix.indexOf(";"));
			
		    byte[] imgBytes = BASE64DecoderStream.decode(data.getBytes());		    
		    BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(imgBytes));
		    rootImage.mkdirs();
		    
		    if(name==null) name= UUID.randomUUID().toString();
		    File imgOutFile = new File(rootImage,name+"."+imgtype);
		    ImageIO.write(bufImg, imgtype, imgOutFile);
		    		    
		    return url+imgOutFile.getName();
			}
		if(!img.matches(url))
			try{					
			System.out.println("download:"+img);			
			URL ur = new URL(img);
			InputStream in = ur.openStream();
			
			if(name==null)								
				name = UUID.randomUUID().toString()+".jpg";				
				
			//String imgtype = name.split("\\.")[1];
			
			File imgOutFile = new File(rootImage,name);
				imgOutFile.getParentFile().mkdirs();
				imgOutFile.createNewFile();
			FileOutputStream out = new FileOutputStream(imgOutFile);
			int size = IOUtils.copy(in, out);
			
			System.out.println("...download:"+url+imgOutFile.getName());
			if(size==0)
				{
				imgOutFile.delete();
				return img;
				}
			return url+imgOutFile.getName();
			}catch (Exception e) {
				e.printStackTrace();
			}
		return img;
		}
	
	public static void main(String[] args)throws Exception {
		Util util = new Util("/Users/max/Downloads","http://");
		//String path = util.saveImage("/Users/max/Downloads/14010_Druckbogen_Muppets_RocknRoll_m.jpg",null);
		//System.out.println(path);
		System.out.println(util.saveImage("http://graph.facebook.com/517267866/picture?type=large"));
		
	}
}

angular.module("ngCommunity",["ngAnimate"])
	
	.controller("loginCtrl",["$scope","$http",function($scope,$http)
		{		
		$scope.open=false;
		$scope.view="login";
		$scope.page=
			{
			remember	:	function(){$scope.view="remember"},
			login		:	function(){$scope.view="login"},
			QR			:	function()
				{
				$scope.view="QR"
				setTimeout(function()
						{
						$("#webcam").show();
						$("video").css(
								{
								position:"absolute",
								width:$("#qrpanel").width(),
								height:$("#qrpanel").height(),
								top:$("#qrpanel").offset().top,
								left:$("#qrpanel").offset().left
								});	
						},1000);
				},
			register	:	function(){$scope.view="register"},
			logged		:	function(){$scope.view="logged"}
			}
		
		$scope.user=
			{
			logged	:	false,
			mail	:	"massimiliano.regis@ekaros.it",
			psw		:	"MACITO",
			psw2	:	"psw"
			}
		$scope.facebook=
			{
			login:function()
				{
				
				}
			}
		$scope.twitter=
			{
			login:function()
				{
				
				}
			}
		$scope.QR=
			{
			login:function()
				{
				
				}
			}
		
		$scope.resetPsw=function()
			{
			
			};
		$scope.lostPsw=function()
			{
			
			};
		$scope.register=function()
			{
			
			};
		$scope.login=function()
			{			
			$http.get("/community/login",{params:{mail:$scope.user.mail,psw:$scope.user.psw}})
			.success(function(data)
				{
				for(item in data)
					$scope.user[item]=data[item];
				$scope.user.logged=true;
				$scope.page.logged();
				})
			.error(function()
				{
				$(document).trigger("community.logerror");
				})
			};
		$scope.logout=function()
			{
			$scope.user.logged=false;
			$scope.view="login";
			}
		}]);
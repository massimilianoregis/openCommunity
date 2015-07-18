angular.module("ngCommunity",["ui.router","ngResource","ngBase"])
.value("config",
	{
	shop:"main",
	community:
		{
		url:{
			user	:	"http://localhost:8080/community/user/:id",
			login	:	"http://localhost:8080/community/login",
			register:	"http://localhost:8080/community/register",
			sendPsw	:	"http://localhost:8080/community/:mail/sendPsw",
			jwt		:	"http://localhost:8080/community/jwt"
			}
		}
	})
.value("user",{logged:false,mail:"massimiliano.regis@gmail.com"})
.config(["$stateProvider","$httpProvider",function($stateProvider,$httpProvider) 
	{	
	$httpProvider.defaults.headers.common["X-Requested-With"]="valore";
	$stateProvider
	.state('community',
		{
		abstract:true,		
		template: '<ui-view/>'		
		})
	.state('profile', 
		{
		url: "/profile",						
		controller:["user","$scope","$state",function(user,$scope,$state)
			{				
			if(!user.logged) 		$state.go("community.login");
			else				 	$state.go("community.profile");
			}]
		})
	 .state('community.confirm', 
		{			
		templateUrl: "views/community/confirm.html",
//		params:["mail","psw"],
		controller:"confirmController"
		})
	 .state('community.lost', 
		{			
		templateUrl: "views/community/resetPsw.html",
		controller:"lostController"
		})
	  .state('community.login', 
		{			
		url:"/community/login.html",
		templateUrl: "views/community/login.html",
		controller:"loginController"
		})
	 .state('community.register', 
		{
		url: "/register",
		templateUrl: "views/community/register.html",
		controller:"registerController"
		})
	.state('community.profile', 
		{		
		url:"/community/profile.html",
		templateUrl: "views/community/profile.html",
		controller:"profileController"
		});
	}])
.factory("community",["$resource","user","$state","config",function($resource,user,$state,config)
	{			
	var userFilter=function(data,user)
		{
		if(data==null) return;
		
		angular.extend(user,data);
		angular.extend(user,user.data);
		
		localStorage.setItem("jwt", user.jwt);
		user.logged=true;
		
		
		}
	var community = $resource(
			config.community.url.user,{id:"@id"},
			{
			login:
				{
				method:'GET',
				params:{mail:'',psw:''},
				url:config.community.url.login,
				responseType:'json',
				transformResponse:function(data)
					{	
					userFilter(data,user);					
					}
				},
			register:
				{
				method:'POST',
				url:config.community.url.register,
				responseType:'json'				
				},
			sendPsw:
				{
				method:'GET',
				url:config.community.url.sendPsw,
				params:{mail:''},
				responseType:'json'
				}
			});
	community.autoLogin=function()
		{
		var jwt = localStorage.getItem("jwt");		
		$.get(config.community.url.jwt,{jwt:jwt}).then(function(data)
			{									
			userFilter(data,user);			
			},function(){
			//$state.go($state.current, {}, {reload: true});
			});
		};
	return community;
	}])
.controller("registerController",["$scope","user","$state","community","loading",function($scope,user,$state,community,loading)
    {
	$scope.mail=user.mail;	
	$scope.register=function()
		{	
		var act = this;
		loading.show();
		community.register(
			{
			mail:this.mail,
			psw: this.psw,
			first_name:this.name,
			last_name:this.surname			
			},function()
			{			
			loading.hide();
			$state.go("community.confirm",{mail:act.mail,psw:act.psw})
			});		
		}
	$scope.login=function()
		{		
		$state.go("community.login");
		}
    }])
.controller("lostController",["$scope","$state","user","community",function($scope,$state,user,community)
    {
	$scope.mail=user.mail;
	$scope.lost=function()
		{		
		community.sendPsw({mail:this.mail});
		}
	$scope.register=function()
		{
		$state.go("community.register");
		}
	$scope.login=function()
		{		
		$state.go("community.login");
		}
    }])
.controller("profileController",["$scope","$state","user","upload","community",function($scope,$state,user,upload,community)
    {
	angular.extend($scope,user);
	$scope.changeBackground=function()
		{				
		upload.upload().then(null,null,function(e)
			{
			user.data.background=e;
			$scope.background=e;
			community.save(user);
			debugger;
			});		
		}
	$scope.changeAvatar=function()
		{		
		upload.upload().then(null,null,function(e)
			{			
			user.data.img=e;
			$scope.img=e;
			community.save(user);
			debugger;
			});				
		}
	$scope.logout=function()
		{
		user.logged=false;
		user.mail=null;
		$state.go("community.login");
		}
	$scope.register=function()
		{
		$state.go("community.register");
		}
	$scope.login=function()
		{		
		$state.go("community.login");
		}
    }])
.controller("confirmController",["$scope","$state","user","community",function($scope,$state,user,community)
    {	
	$scope.confirm=function()
		{				
		community.login({mail:$state.params.mail,psw: $state.params.psw},function(data)
			{			
			$state.go("community.profile")
			});
		}
	$scope.register=function()
		{
		$state.go("community.register");
		}
	$scope.login=function()
		{		
		$state.go("community.login");
		}
    }])
.controller("loginController",["$scope","$state","user","community","loading",function($scope,$state,user,community,loading)
	{		
	console.log("-->loginController");
	$scope.mail="massimiliano.regis@ekaros.it";
	$scope.psw="pippo";
	$scope.lost=function()
		{		
		user.mail=$scope.mail;
		$state.go("community.lost")
		}
	$scope.register=function()
		{
		user.mail=$scope.mail;
		$state.go("community.register")
		}
	$scope.login=function()
		{
		loading.show();
		console.log("login..."+this.mail+" "+this.psw)
		community.login({mail:this.mail,psw: this.psw},function(data)
			{
			console.log("login OK");
			loading.hide();
			$state.go("community.profile");
			},function()
			{
			console.log("login KO")
			loading.hide();
			});		
		}
	console.log("<--loginController");
	}])
.run(["community",function(community)
    {
	community.autoLogin();	
	//debugger;
    }])
	/*
.directive('equals', function() {
	  return {
	    restrict: 'A', // only activate on element attribute
	    require: '?ngModel', // get a hold of NgModelController
	    link: function(scope, elem, attrs, ngModel) {
	      if(!ngModel) return; // do nothing if no ng-model

	      // watch own value and re-validate on change
	      scope.$watch(attrs.ngModel, function() {
	        validate();
	      });

	      // observe the other value and re-validate on change
	      attrs.$observe('equals', function (val) {
	        validate();
	      });

	      var validate = function() {
	        // values
	        var val1 = ngModel.$viewValue;
	        var val2 = attrs.equals;
	        // set validity
	        ngModel.$setValidity('equals', ! val1 || ! val2 || val1 === val2);
	      };
	    }
	  }
	});*/

var communityApp = angular.module('communityApp', ['ngRoute','ngAnimate','bootstrap-tagsinput']);

communityApp.config(function($routeProvider) 
	{
	$routeProvider
		.when('/', {
			templateUrl : 'pages/users.html'
		})
		.when('/users', {
			templateUrl : 'pages/users.html'
		})
		.when('/config', {
			templateUrl : 'pages/config.html'
		})
		.when('/user', {
			templateUrl : 'pages/user.html'			
		})
		.when('/user/:id', {
			templateUrl : 'pages/user.html'			
		});
	});

communityApp.controller('mainController', function($scope) {

		// create a message to display in our view
		$scope.message = 'Everyone come and see how good I look!';
	});
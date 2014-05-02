'use strict';

/**
 * @ngdoc service 
 * @name dntApp.authorization
 * @description The authorization handles posting login and logout request to server.
 * @requires $http 
**/
angular.module('dntApp').factory('authorization', ['$http','$location', function ($http, $location) {

	return {
		newLogin: function() {
			return $http.get('api/login/setup');
		},
		checkLogin: function(data, hmac) {
			var credentials = {'data' : data, 'hmac' : hmac}
			return $http.post('/api/login/checkLogin', credentials);
		},
		
		login: function (credentials) {
			return $http.post('/login', credentials);
		},

		logout: function () {
			return $http.post('/logout');
		},
	};
	
	
}]);

angular.module('dntApp').factory('appStateService', ['$log','$cookieStore', '$location', function ($log,$cookieStore, $location) {

	return {
		saveAttemptUrl: function() {
			if($location.path().toLowerCase() != '/login') {
				$cookieStore.put('redirectUrl', $location.url());
			}
		},
		
		redirectToLogin: function() {
				$location.path('/login');
		},
		
		redirectToAttemptedUrl: function() {
			$location.$$search = {};
			var url = $cookieStore.get('redirectUrl');
			$cookieStore.remove('redirectUrl');
			if(angular.isUndefined(url)) {
				$location.path('/');
			}
			else {
				$location.url(url);
			}
		},
		
		removeUserCredentials: function () {
			$cookieStore.remove('token');
			$cookieStore.remove('name');
			$cookieStore.remove('isAdmin');
		},
		
		insertUserCredentials: function (token, name, isAdmin) {
			$cookieStore.put('token', token);
			$cookieStore.put('name', name);
			$cookieStore.put('isAdmin', isAdmin);
		},
		
		getUserCredentials: function () {
			var cred = {};
			cred.token = 	$cookieStore.get('token');
			cred.name = 	$cookieStore.get('name');
			cred.isAdmin = 	$cookieStore.get('isAdmin');
			return cred;
		}
		
	};
	
	
}]);

/*
 * httpInterceptor will intercept a unauthorized access and redirect
 * user to /login view.
 */
/**
 * @ngdoc service 
 * @name dntApp.httpInterceptor
 * @description httpInterceptor will intercept a unauthorized access and redirect
 * user to /login view.
 * @requires $http 
**/
angular.module('dntApp').factory('httpInterceptor', ['appStateService', '$q', '$window',  function httpInterceptor (appStateService, $q, $window) {
	return function (promise) {
		var success = function (response) {
			return response;
		};

		var error = function (response) {
			if (response.status === 401) {
				appStateService.saveAttemptUrl();
				appStateService.redirectToLogin();
			}

			return $q.reject(response);
		};

		return promise.then(success, error);
	};
}]);


/**
 * @ngdoc service 
 * @name dntApp.api
 * @description Puts the authentication token into the header of http request done
 * by client. 
 * Token is retrieved either from cookieStore (logged in from a previous session)
 * or sent as a method parameter.
 * @requires $http 
**/
angular.module('dntApp').factory('api', ['$http', 'appStateService', function ($http, appStateService) {
	return {
		init: function (token) {
			$http.defaults.headers.common['X-AUTH-TOKEN'] = token || appStateService.getUserCredentials().token;
		}
	};
}]);
'use strict';

/**
 * @ngdoc service 
 * @name dntApp.authorization
 * @description The authorization handles posting login and logout request to server.
 * @requires $http 
**/
angular.module('dntApp').factory('authorization', ['$http', '$cookieStore', function ($http, $cookieStore) {

	return {
		newLogin: function() {
			return $http.get('api/login/setup');
		},
		checkLogin: function(data, hmac) {
			console.log("kalles i auth")
			var credentials = {'data' : data, 'hmac' : hmac}
			return $http.post('/api/login/checkLogin', credentials);
		},
		
		login: function (credentials) {
			return $http.post('/login', credentials);
		},

		logout: function () {
			return $http.post('/logout');
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
angular.module('dntApp').factory('httpInterceptor', ['$q', '$window', '$location', function httpInterceptor ($q, $window, $location) {
	return function (promise) {
		var success = function (response) {
			return response;
		};

		var error = function (response) {
			if (response.status === 401) {
				$location.url('/login');
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
angular.module('dntApp').factory('api', ['$http', '$cookieStore', function ($http, $cookieStore) {
	return {
		init: function (token) {
			$http.defaults.headers.common['X-AUTH-TOKEN'] = token || $cookieStore.get('token');
		}
	};
}]);
'use strict';

/**
 * @ngdoc service 
 * @name dntApp.authorization
 * @description The authorization handles posting login and logout request to server.
 * @requires $http 
**/
angular.module('dntApp').factory('authorization', ['$http', function ($http) {

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

/**
 * @ngdoc service 
 * @name dntApp.appStateService
 * @requires $cookieStore 
 * @requires $location
 * @description The àppStateService` is a service used to persist user credentials and 
 * location of app when redirecting to an external site. The data is persisted using cookieStore.
 *
**/
angular.module('dntApp').factory('appStateService', ['$cookieStore', '$location', function ($cookieStore, $location) {

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
		
		insertUserCredentials: function (token, id, name, isAdmin, email) {
			$cookieStore.put('token', token);
			$cookieStore.put('id', id);
			$cookieStore.put('name', name);
			$cookieStore.put('isAdmin', isAdmin);
			$cookieStore.put('email', email);
		},
		
		getUserCredentials: function () {
			var cred = {};
			cred.token = 	$cookieStore.get('token');
			cred.name = 	$cookieStore.get('name');
			cred.isAdmin = 	$cookieStore.get('isAdmin');
			cred.email = 	$cookieStore.get('email');
			cred.id = 		$cookieStore.get('id');
			return cred;
		}
		
	};
	
	
}]);

/**
 * @ngdoc service 
 * @name dntApp.httpInterceptor
 * @requires $q 
 * @requires appStateService 
 * @description httpInterceptor will intercept a unauthorized access, save location of
 * app and redirect user to /login view using àppStateService`
 * 
**/
angular.module('dntApp').factory('httpInterceptor', ['appStateService', '$q',  function httpInterceptor (appStateService, $q) {
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
 * @requires $http 
 * @requires appStateService 
 * @description Puts the authentication token into the header of http request done
 * by client. 
 * Token is retrieved either from cookieStore (logged in from a previous session)
 * or sent as a method parameter.
 * 
**/
angular.module('dntApp').factory('api', ['$http', 'appStateService', function ($http, appStateService) {
	return {
		init: function (token) {
			$http.defaults.headers.common['X-AUTH-TOKEN'] = token || appStateService.getUserCredentials().token;
		}
	};
}]);
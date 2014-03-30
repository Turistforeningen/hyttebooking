'use strict';
/*
 * The authorization handles posting login and logout request to server.
 * 
 */

angular.module('dntApp').factory('authorization', ['$http', function ($http) {

	return {
		login: function (credentials) {
			return $http.post('/login', credentials);
		},

		logout: function () {
			return $http.post('/logout');
		}
	};
}]);

/*
 * httpInterceptor will intercept a unauthorized access and redirect
 * user to /login view.
 */
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


/*
 *Puts the authentication token into the header of http request done
 *by client. 
 *Token is either stored in a cookie (from a previous session)
 *or sent as a method parameter. 
 */
angular.module('dntApp').factory('api', ['$http', '$cookieStore', function ($http, $cookieStore) {
	return {
		init: function (token) {

			$http.defaults.headers.common['X-AUTH-TOKEN'] = token || $cookieStore.get('token');
		}
	};
}]);
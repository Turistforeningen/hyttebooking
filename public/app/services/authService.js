app.factory('authorization', function ($http, $log) {
 

	return {
		login: function (credentials) {
			return $http.post('/login', credentials);
		},

		logout: function () {
			$log.info("ldfdfol")
			return $http.post('/logout');
		}
	};
});

app.factory('httpInterceptor', function httpInterceptor ($q, $window, $location) {
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
	});


app.factory('api', function ($http, $cookieStore, $log) {
	 return {
	      init: function (token) {
	    	 
	          $http.defaults.headers.common['X-AUTH-TOKEN'] = token || $cookieStore.get('token');
	      }
	  };
	});
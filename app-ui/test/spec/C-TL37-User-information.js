//AUTHCONTROLLER -responsible for DNT flow, and /login and authview
describe('authController', function(){
    var scope,mockService, $location, $rootScope, createController, appStateService, routeParams, $http, $httpBackend, api, $window;
    
    
    beforeEach(inject(function($injector) {
    	$http = $injector.get('$http');
    	$window = {'location' : {'href' : 'http://www.fake.com'}}
    	$httpBackend = $injector.get('$httpBackend');
        $location = $injector.get('$location');
        $rootScope = $injector.get('$rootScope');
        api = $injector.get('api');
        	
        mockService = $injector.get('authorization');
        spyOn(mockService, 'newLogin').andCallThrough();
        spyOn(mockService, 'checkLogin').andCallThrough();
        spyOn(mockService, 'logout').andCallThrough();
        
        routeParams = {};
        appStateService = $injector.get('appStateService');
        scope = $rootScope.$new();
        
        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('authController', {
                '$scope': scope,
                'appStateService' : appStateService,
                'authorization' : mockService,
                '$routeParams': routeParams,
                '$location' : $location,
                'api' : api,
                '$window' : $window
            });
        };
    }));
    
    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
      });
    
    it('should emit event and remove cookie at logout at logout and http header removed' , function() {
    	spyOn(appStateService, 'removeUserCredentials').andCallThrough();
    	spyOn(scope, "$emit");
    	$httpBackend.when('POST', '/logout').respond({'status': 'ok'});
    	$httpBackend.expectPOST('/logout');
    	var token = '12345'
    	var data = {'id': 12345,'authToken': '123ABC', 'name': 'ola', 'email': 'o@g.com', 'isAdmin' : false};
    	api.init(token);
    	appStateService.insertUserCredentials(data.authToken, data.id, data.name, data.isAdmin, data.email);
    	
    	var controller = createController();
    	scope.logout();
    	$httpBackend.flush();
    	scope.$apply();
    	expect(mockService.logout).toHaveBeenCalled();
    	expect(scope.loginErrorMessage).toBe('');
    	expect(scope.$emit).toHaveBeenCalledWith('event:signedOut');
    	expect(appStateService.removeUserCredentials).toHaveBeenCalled();
    	
    	var cred = appStateService.getUserCredentials();
    	expect(cred.id).toBeUndefined();
    	expect(cred.name).toBeUndefined();
    	expect(cred.token).toBeUndefined();
    	expect(cred.email).toBeUndefined();
    	expect(cred.isAdmin).toBeUndefined();
    	//removed at actual backend but should probably be removed at front end.
    	expect($http.defaults.headers.common['X-AUTH-TOKEN']).toBeUndefined();
    	
    });
    
    it('should still log out front end even if there back end can be contacted' , function() {
    	spyOn(appStateService, 'removeUserCredentials').andCallThrough();
    	spyOn(scope, "$emit");
    	$httpBackend.when('POST', '/logout').respond(500, '');
    	$httpBackend.expectPOST('/logout');
    	var token = '12345'
    	var data = {'id': 12345,'authToken': '123ABC', 'name': 'ola', 'email': 'o@g.com', 'isAdmin' : false};
    	api.init(token);
    	appStateService.insertUserCredentials(data.authToken, data.id, data.name, data.isAdmin, data.email);
    	
    	var controller = createController();
    	scope.logout();
    	scope.$apply();
    	$httpBackend.flush();
    	expect(mockService.logout).toHaveBeenCalled();
    	expect(scope.loginErrorMessage.length>0).toBe(true);
    	expect(scope.$emit).toHaveBeenCalledWith('event:signedOut');
    	expect(appStateService.removeUserCredentials).toHaveBeenCalled();
    	
    	var cred = appStateService.getUserCredentials();
    	expect(cred.id).toBeUndefined();
    	expect(cred.name).toBeUndefined();
    	expect(cred.token).toBeUndefined();
    	expect(cred.email).toBeUndefined();
    	expect(cred.isAdmin).toBeUndefined();
    	//removed at actual backend but should probably be removed at front end.
    	expect($http.defaults.headers.common['X-AUTH-TOKEN']).toBeUndefined();
    	
    });
    
   
});
'use strict';

beforeEach(module('dntCommon'));
describe('headerController', function(){
    var scope, $location, $rootScope, createController, appStateService;

    beforeEach(inject(function($injector) {
        $location = $injector.get('$location');
        $rootScope = $injector.get('$rootScope');
        appStateService = $injector.get('appStateService');
        scope = $rootScope.$new();
        
        var $controller = $injector.get('$controller');

        createController = function() {
            return $controller('headerController', {
                '$scope': scope, "appStateService" : appStateService
            });
        };
    }));
    
    it('should call method init on initialization', function() {
    	 	
    	spyOn(appStateService, "getUserCredentials").andCallThrough();
    	var controller = createController();
    	
    	//init has been run at startup run if this is true
    	expect(appStateService.getUserCredentials).toHaveBeenCalled();
    });
    
    
    it('should have these properties', function() {
    	
    	var controller = createController();
    	expect(scope.name).toBeDefined();
    	expect(scope.isAdmin).toBeDefined();
    	expect(scope.loggedIn).toBeDefined();
    	expect(typeof scope.name).toBe('string');
    	expect(typeof scope.isAdmin).toBe('boolean');
    	expect(typeof scope.loggedIn).toBe('boolean');
    });
    
    
    it('should have loggedIn to false since user is not in cookieStore', function() {
        var controller = createController();
        expect(scope.loggedIn).toBe(false);
        expect(scope.name).toBe('');
    });
    
    
    it('should have loggedIn set to true since user is in cookieStore', function() {
    	appStateService.insertUserCredentials("abc123", 45435, "Ola Nordmann", false, "ola@nordmann.no");
    	scope.$apply();
    	var controller = createController();
        expect(scope.loggedIn).toBe(true);
        expect(scope.name).toBe('Ola Nordmann');
        expect(scope.token).toBeUndefined();
        expect(scope.isAdmin).toBe(false);
    });
    
    it('should listen to event:signedIn and set state accordingly', function() {
    	var controller = createController();
        expect(scope.loggedIn).toBe(false);
        $rootScope.$broadcast('event:signedIn', {'name': 'ola nordmann', 'isAdmin' : true});
        scope.$apply;
        
        expect(scope.loggedIn).toBe(true);
        expect(scope.name).toBe("ola nordmann");
    });
    
    it('should listen to event:signedIn and set state accordingly', function() {
    	var controller = createController();
    	scope.loggedIn = true;
    	scope.isadmin = true;
    	scope.name = 'name';
        expect(scope.loggedIn).toBe(true);
        $rootScope.$broadcast('event:signedOut', null);
        scope.$apply;
        
        expect(scope.loggedIn).toBe(false);
        expect(scope.name).toBe("");
        expect(scope.isAdmin).toBe(false);
    });
});







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
    
    it('should init controller without calling checkLogin if no routeParams is present', function() {
    	
    	var controller = createController();
    	expect(mockService.checkLogin).not.toHaveBeenCalled();
    	expect(scope.showLogin).toBe(false);
 
    });
    
    it('should call checkLogin from init if hmac and encryptedData is present', function() {
    	
    	routeParams.data = '12345';
    	routeParams.hmac = '54231';
    	$httpBackend.when('POST', '/api/login/checkLogin').respond({'id': 12345,'authToken': '123ABC', 'name': 'ola', 'email': 'o@g.com', 'isAdmin' : false});
    	$httpBackend.expectPOST('/api/login/checkLogin');
    	
    	expect(mockService.checkLogin).not.toHaveBeenCalled();
    	var controller = createController();
    	spyOn(scope, 'checkLogin').andCallThrough();
    	scope.$apply();
    	
    	$httpBackend.flush();
    	expect(mockService.checkLogin).toHaveBeenCalled();
    	expect(mockService.checkLogin).toHaveBeenCalledWith(routeParams.data, routeParams.hmac);
    	expect(scope.showLogin).toBe(false);
 
    });
    
    it('should not call checkLogin from init if one of routeParams missing', function() {
    	
    	routeParams.data = '12345';
    	//routeParams.hmac = '54231';
    	
    	expect(mockService.checkLogin).not.toHaveBeenCalled();
    	var controller = createController();
    	spyOn(scope, 'checkLogin').andCallThrough();
    	scope.$apply();
    	expect(mockService.checkLogin).not.toHaveBeenCalled();
    	
    	routeParams.data = null;
    	routeParams.hmac = '54231';
    	
    	var controller = createController();
    	spyOn(scope, 'checkLogin').andCallThrough();
    	scope.$apply();
    	expect(mockService.checkLogin).not.toHaveBeenCalled();
 
    });
    
    it('should set showLogin to true if location path is /login' , function() {

    	$location.path('/login');
    	var controller = createController();
    	
    	expect(scope.showLogin).toBe(true);
    	
    });
    
    it('should set an error message if checkLogin fails' , function() {
  
    	$httpBackend.when('POST', '/api/login/checkLogin').respond(401, '');
    	$httpBackend.expectPOST('/api/login/checkLogin');
    	var controller = createController();
    	scope.checkLogin('NA', 'NA');
    	$httpBackend.flush();
    	expect(mockService.checkLogin).toHaveBeenCalled();
    	expect(scope.loginErrorMessage).toBeDefined();
    	expect(scope.loginErrorMessage.length>0).toBe(true);
    	
    });
    
    it('should put user creds in cookies, emit a event and put token in header' , function() {
    	
    	var data = {'id': 12345,'authToken': '123ABC', 'name': 'ola', 'email': 'o@g.com', 'isAdmin' : false};
    	$httpBackend.when('POST', '/api/login/checkLogin').respond(data);
    	$httpBackend.expectPOST('/api/login/checkLogin');
    	var controller = createController();
    	spyOn(appStateService, 'insertUserCredentials').andCallThrough();
    	spyOn(appStateService, 'redirectToAttemptedUrl').andCallThrough();
    	spyOn(scope, "$emit");
    	
    	scope.checkLogin('NA', 'NA');
    	$httpBackend.flush();
    	expect(mockService.checkLogin).toHaveBeenCalled();
    	expect(scope.loginErrorMessage.length).toBe(0);
    	
    	//User data put in cookieStore

    	expect(appStateService.insertUserCredentials).toHaveBeenCalled();
    	expect(appStateService.redirectToAttemptedUrl).toHaveBeenCalled();
    	var cred = appStateService.getUserCredentials();
    	expect(cred.name).toBe(data.name);
    	expect(cred.token).toBe(data.authToken);
    	expect(cred.email).toBe(data.email);
    	expect(cred.isAdmin).toBe(data.isAdmin);
    	
    	//check if checkLogin emits a event

    	expect(scope.$emit).toHaveBeenCalledWith("event:signedIn", data);
    	
    	//check if token put in header (api.init(token))
    
    	expect($http.defaults.headers.common['X-AUTH-TOKEN']).toBe(data.authToken);
    });
    
    //Tested in C-TL37-User-information
    /*
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
    */
    it('should redirect if successful call to server with newLogin' , function() {
    	var data = {"redirectUrl" : "http://www.vg.no"};
    	$httpBackend.when('GET', 'api/login/setup').respond(data);
    	$httpBackend.expectGET('api/login/setup');
    	var token = '12345'
    	
    	//$window is a fake object in this test
    	expect($window.location.href).toBe('http://www.fake.com');
    	var controller = createController();
    	scope.newLogin();
    	scope.$apply();
    	$httpBackend.flush();
    	scope.$apply();
    	expect(mockService.newLogin).toHaveBeenCalled();
    	expect(scope.loginErrorMessage.length>0).toBe(false);
    	expect($window.location.href).toBe(data.redirectUrl);
    	expect($http.defaults.headers.common['X-AUTH-TOKEN']).toBeUndefined();
    	
    });
});


//check html?
//Check services.
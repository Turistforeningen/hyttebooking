'use strict';

beforeEach(module('dntApp'));
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
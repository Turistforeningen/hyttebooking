'use strict';

describe('dntSelector', function () {


    var scope, elm, $body = $('body');

    //Inject rootscope and comile and use them to set up directive for testing
    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();
        scope.beds = 10;
        scope.guests = [{type: "ungdom", nr: 0, price:300}, {type: "barn", nr: 0, price:500}];
        scope.hider = 1;
       
    }));
    
    function compileDirective(tpl) {
        if (!tpl) tpl = '<dnt-selector person-types="guests" hide-type-index="hider" number-of-beds="beds"></dnt-selector>';
    
        // inject allows you to use AngularJS dependency injection
        // to retrieve and use other services
        var element = angular.element(tpl);
        inject(function($compile) {
            elm = $compile(element)(scope);

        });
        $body.append(elm);
        // $digest is necessary to finalize the directive generation
        scope.$digest();
    }
    
    describe('initialisation', function() {
        // before each test in this block, generates a fresh directive
        beforeEach(function() {
            compileDirective();
        });
        // a single test example, check the produced DOM
        it('should produce 2 drop down and be collapsed', function() {
        	var dirScope = elm.isolateScope()
        	expect(elm.hasClass("selectNumber")).toBeTruthy;
        	expect(elm.find('select').length).toEqual(2);
        	//each drop down contains 11 options. Check html for this
        	expect(elm.find('option').length).toEqual(11*2);
        	expect(dirScope.isCollapsed).toBe(true);
        	expect(dirScope.person).toBe(scope.guests);
        	
        });
        
        it('should produce correct inital beds left and options ranges used', function() {
        	var dirScope = elm.isolateScope();
        	expect(dirScope.bedsLeft()).toBe(10);
        	expect(dirScope.range(0).length).toBe(11);
        });
    });
    
    describe('range construction', function() {
        // before each test in this block, generates a fresh directive
        beforeEach(function() {
            compileDirective();
        });
        // a single test example, check the produced DOM
        it('watch should detect changes nr of the person types json', function() {
        	var dirScope = elm.isolateScope()
        	spyOn(dirScope, 'constructRange').andCallThrough();
        	expect(dirScope.constructRange).not.toHaveBeenCalled();
        	dirScope.person[0].nr = 1;
        	console.log(dirScope.person);
        	scope.$digest();
        	
        	expect(dirScope.constructRange).toHaveBeenCalled();
        	expect(dirScope.range(1)).not.toBeUndefined();
        	expect(dirScope.range(1).length).toBe(11);
        	expect(dirScope.range(0).length).toBe(10);
        	expect(dirScope.range(2)).toBeUndefined();
        });
        
        it('should return no other option than zero or already selected value if cabin maxed out', function() {
        	var dirScope = elm.isolateScope()
        	spyOn(dirScope, 'constructRange').andCallThrough();
        	//maxed out first category
        	dirScope.person[0].nr = 10;
        	scope.$digest();
        	expect(dirScope.constructRange).toHaveBeenCalled();
        	//for all categories with nr set to 0 should only display
        	//0 beds in drop down
        	expect(dirScope.range(0).length).toBe(1);
        	expect(dirScope.range(10).length).toBe(11);
        });
        
        
    });
});
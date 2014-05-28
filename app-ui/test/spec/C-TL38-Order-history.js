'use strict';

beforeEach(module('dntApp'));
describe('orderController', function () {


    var scope, fakeFactory, controller, q, deferred, routeParams, mockService;

    //Prepare the fake factory
    beforeEach(function () {
        mockService = {
            getOrders: function (page, pageSize) {
                deferred = q.defer();
                // Place the fake return object here
                var returnData = {"data": [{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}],"totalItems":1};
                deferred.resolve(returnData);
                return deferred.promise;
            }
        };
        spyOn(mockService, 'getOrders').andCallThrough();
    });

    //Inject fake factory into controller
    beforeEach(inject(function ($rootScope, $controller, $q, $routeParams) {
        scope = $rootScope.$new();
        q = $q;
        routeParams = $routeParams;
        controller = $controller('orderController', { $scope: scope, $routeParams: routeParams, bookingService: mockService });
    }));

    it('The order list object is not defined yet', function () {
        // Before $apply is called the promise hasn't resolved
        expect(scope.itemsPerPage).toBe(10);
        expect(scope.orders).not.toBeDefined();
    });

    it('Applying the scope causes it to be defined', function () {
        // This propagates the changes to the models
        // This happens itself when you're on a web page, but not in a unit test framework
        scope.$apply();
        expect(scope.orders).toBeDefined();
    });

    it('Ensure that the method was invoked', function () {
        scope.$apply();
        expect(mockService.getOrders).toHaveBeenCalled();
    });

    it('Check the value returned', function () {
        scope.$apply();
        var compareData = [{"ableToCancel":true,"cabin":{"cabinType":"large","id":1,"name":"Fjordheim","nrActiveBookings":1},"dateFrom":1399586400000,"dateTo":1399759200000,"id":2,"nrOfBeds":"3","status":0,"timeOfBooking":1398328377255}];
        expect(scope.orders).toEqual(compareData);
        expect(scope.totalItems).toBe(1);
        expect(scope.orders[0].ableToCancel).toBe(true);
    });
    
    it('setPage should set page variable to x-1 of all input parameters (since bootstrap ui pagination)', function() {
        
        scope.setPage(4);
        scope.$apply();
        //server pages zero indexed 4-1 = 3
        expect(mockService.getOrders).toHaveBeenCalledWith(3, 10);
        scope.setPage(-5);
        scope.$apply();
        expect(mockService.getOrders).not.toHaveBeenCalledWith(-6, 10);
        //currentPage contain the paginations current page.
        expect(scope.currentPage).toBe(4);
    })
});

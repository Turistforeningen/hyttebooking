angular.module('dntBookingModule', [])
.directive('dntSelector', function() {
    return {
        restrict: 'AE',
        
        scope: {'data': '=personTypes',
        		'hider': '=hideTypeIndex',
        		'beds' : '=numberOfBeds'
        },
        
        template: 
        ' <div class="row" ng-repeat="per in person.slice(0, hider)">'+
        '<div class="col-lg-9 col-md-8"><p>{{per.type}}<p></div>'+
        '<div class="col-lg-3 col-md-4"><select class="selectNumber" ng-model="per.nr"'+
        'ng-options="pe for pe in range(per.nr)"></select></div></div>'+
        '<div class="row"><div class="col-lg-12 col-md-12"><a ng-click="toggleHide()">Ikke medlem?</a></div></div>'+
        ' <div class="row" ng-hide="hide" ng-repeat="per in person.slice(hider)">'+
        '<div class="col-lg-9 col-md-8"><p>{{per.type}}<p></div>'+
        '<div class="col-lg-3 col-md-4"><select class="selectNumber" ng-model="per.nr"'+
        'ng-options="pe for pe in range(per.nr)"></select></div></div>',
        
        controller: function($scope, $log) {
        	$scope.person = {};
        	$scope.hide = true;
        	
        	$scope.toggleHide = function() {
        		$scope.hide = !$scope.hide;
        	}
        	$scope.setPerson = function(person) {
        		$scope.person = person;
        	};
        	$scope.getPerson = function(person) {
        		return $scope.person;
        	};
        	
        	$scope.bedsLeft = function() {

        		var left = $scope.beds;
        		angular.forEach($scope.person, function(value, key) {
        			left = left -value.nr;
        		});
        		if(left>0) {
        			return left;
        		}
        		else {

        			return 0;
        		}
        	};

        	$scope.range = function(value) {
        		
        		var bedsLeft = $scope.bedsLeft();
        		var end = bedsLeft;
        		if((value != null || value>0) && end<=value) {
        			end = value + bedsLeft;
        		}

        		var result = [];
        		for (var i = 0; i <= end; i++) {
        			result.push(i);
        		}

        		return result;
        	};

        },
        
        link: function(scope, elem, attrs) {
        	scope.setPerson(scope.data);
        	scope.$watch('data', function(newValue, oldValue) {
                if (newValue)
                    scope.setPerson(newValue);
                	
            });
        	
        }
    };
});
angular.module('dntBookingModule')
.directive('dntPriceViewer', function() {
    return {
        restrict: 'AE',
        
        scope: {'data': '=personTypes',
        	'fromDate': '=',
        	'toDate' : '='
        	
        },
        
        template:
        '<div class="row" style="min-height: 250px;">' +
        
        '<table class="table table-condensed">' +
        '<tr ng-repeat="person in personType" ng-show="person.nr>0">'+
        '<td>{{person.type}}</td>'+
        '<td>x{{person.nr}}</td>' +
        '<td>{{days}}</td>'+
        '<td>{{person.nr * person.price * days}}</td>'+
        '</tr></table>'+
        '</div>'+
        '<div class=row><div class="col-lg-7 col-md-7"><p><strong>Totalt beløp</strong></p></div>'+
        '<div class="col-lg-5 col-md-5"><p><strong>{{price}} NOK</strong></p></div></div>',
       
        controller: function($scope, $log) {
        	$scope.personType = {};
        	$scope.days =1;
        	$scope.fromDate;
        	$scope.toDate;
        	
        	$scope.setPersonType = function(person) {
        		$scope.personType = person;
        	};
        	$scope.getPerson = function(person) {
        		return $scope.person;
        	};
        	$scope.calculatePrice = function() {
        		var totalPrice = 0;
        		angular.forEach($scope.personType, function(value, key) {
        			totalPrice +=(value.nr * value.price*$scope.days);
        			
        		});
        		$scope.price = totalPrice;
        	};
        	
        	$scope.newDateRange = function() {
        		if($scope.fromDate  && $scope.toDate) {
        			
        		var d1 = new Date($scope.fromDate);
        		var d2 = new Date($scope.toDate);
        		var miliseconds = d2-d1;
        		var seconds = miliseconds/1000;
        		var minutes = seconds/60;
        		var hours = minutes/60;
        		var days = hours/24;
        		if(days <0 || isNaN(d1.getTime()) || isNaN(d2.getTime()) ) {
        			$scope.days = 1;
        		}
        		else {
        			$log.info(days);
        			//ceiling will fail if a booking span serveral years.
        			//days method not 100% accurate, but close.
        			$scope.days = Math.ceil(days);
        		}
        		$scope.calculatePrice();
        		}
        	};
        	
        },
        
        link: function(scope, elem, attrs) {
        	scope.setPersonType(scope.data);
        	scope.$watch('data', function(newValue, oldValue) {
                if (newValue)
                	scope.setPersonType(newValue);
                    scope.calculatePrice();
            }, true);
        	
        	
        	
        	scope.$watch('fromDate', function(newValue, oldValue) {
                if (newValue)
                    scope.newDateRange();
            });
        	
        	scope.$watch('toDate', function(newValue, oldValue) {
                if (newValue)
                    scope.newDateRange();
            });
        }
    };
});
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
        '<div class="col-lg-9 col-md-9"><p>{{per.type}}<p></div>'+
        '<div class="col-lg-3 col-md-3"><select class="selectNumber" ng-model="per.nr"'+
        'ng-options="pe for pe in range(per.nr)"></select></div></div>'+
        '<div class="row"><div class="col-lg-12 col-md-12"><a ng-click="toggleHide()">Ikke medlem?</a></div></div>'+
        ' <div class="row" ng-hide="hide" ng-repeat="per in person.slice(hider)">'+
        '<div class="col-lg-9 col-md-9"><p>{{per.type}}<p></div>'+
        '<div class="col-lg-3 col-md-3"><select class="selectNumber" ng-model="per.nr"'+
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
        }
    };
});
angular.module('dntBookingModule')
.directive('dntPriceViewer', function() {
    return {
        restrict: 'AE',
        
        scope: {'data': '=personTypes'
        		
        },
        
        template: 
        ' <div class="row" ng-repeat="person in personType">'+
        '<div class="col-lg-12 col-md-12" ng-show="person.nr>0">'+
        '<p>{{person.type}}  x{{person.nr}}  {{person.nr * person.price}}</p>'+
        '</div></div>',
       
        controller: function($scope, $log) {
        	$scope.personType = {};
        	
        	$scope.setPersonType = function(person) {
        		$scope.personType = person;
        	};
        	$scope.getPerson = function(person) {
        		return $scope.person;
        	};
        	
        	
        },
        
        link: function(scope, elem, attrs) {
        	scope.setPersonType(scope.data);
        }
    };
});
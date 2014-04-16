'use strict';

/**
 * @ngdoc directive
 * @name dntBookingModule.directive:dntSelector
 * @element div
 * @function
 * @restrict AE
 * @param  {json array} personTypes  json array with supported guest types for cabin
 * @param  {number} hideTypeIndex  If there are many categories, a number can be used to hide the rest of the categories
 * @param  {number} numberOfBeds  The max bed capacity of the cabin. Prohibits user from selecting more beds than can be actually booked.
 * @description
 * Displays a number of selects decided by the number of guestTypes.
 * It will prevent the user from selecting guests from each category such that
 * the total number of guests are over the cabin capacity.
 * 
 *
 * **Note:** PersonType should be of the form [{"type": "x", "price": "x", "nr":"x"}, ... , {"type": "x", "price": "x", "nr":"x"}]
 *
 * @example
   	<example module="dntApp">
   		<file name="script.js">
             angular.module('dntApp', ['ui.bootstrap','dntBookingModule'])
             .controller('selectCtrl', ['$scope',function ($scope) { 
                 $scope.guests = [{"nr": 0,"price": 300,"type": "Voksen, medlem"},{"nr": 0,"price": 150,"type": "Ungdom, medlem"},{"nr": 0,"price": 150,"type": "Ungdom"}];
            		$scope.beds = 20;
            		$scope.hide = 2;
             }]);
         </file>
         <file name="index.html">
   			<div ng-controller="selectCtrl">
 
   				<div dnt-selector person-types="guests" hide-type-index="hide" number-of-beds="beds">
   				</div>
   				</div>
  				
			
		</file>
 	</example>
 */
angular.module('dntBookingModule', [])
.directive('dntSelector', function() {
	return {
		restrict: 'AE',

		scope: {'data': '=personTypes',
			'hider': '=hideTypeIndex',
			'beds' : '=numberOfBeds'
		},

		template:
			'<div class="row" ng-repeat="per in person.slice(0, hider)">'+
			'<div class="col-lg-8 col-md-8">'+
			'<p>{{per.type}}</p>'+
			'</div>'+
			'<div class="col-lg-4 col-md-4 modRightText">'+
			'<select class="selectNumber" ng-model="per.nr" ng-options="pe for pe in range(per.nr)"></select>'+
			'</div>'+
			'</div>'+
			'<div class="row">'+
			'<div class="col-lg-12 col-md-12">'+
			'<br>'+
			'<a ng-click="toggleCollapsed()" href="" ng-controller="TooltipDemoCtrl" tooltip-placement="top" tooltip-html-unsafe="{{tooltipNoneMember}}" tooltip-popup-delay="1200">Ikke medlem?</a>'+
			'<br><br>'+
			'<div collapse="isCollapsed">'+
			'<div class="row" ng-repeat="per in person.slice(hider)">'+
			'<div class="col-lg-8 col-md-8">'+
			'<p>{{per.type}}</p>'+

			'</div>'+
			'<div class="col-lg-4 col-md-4 modRightText">'+
			'<select class="selectNumber" ng-model="per.nr" ng-options="pe for pe in range(per.nr)"></select>'+
			'</div>'+
			'</div>'+
			'</div>'+

			'</div>'+
			'</div>',

		controller: ['$scope','$log', function($scope,$log) {
				$scope.person = {};
				$scope.isCollapsed = true;

				$scope.toggleCollapsed = function() {
					$scope.isCollapsed = !$scope.isCollapsed;
					//When a user shuts down this collapse all entries inside collapse should be erased here
				};
				$scope.setPerson = function(person) {
					$log.info("lol");
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
					if((value !== null || value>0) && end<=value) {
						end = value + bedsLeft;
					}

					var result = [];
					for (var i = 0; i <= end; i++) {
						result.push(i);
					}

					return result;
				};

			}],

		link: function(scope, elem, attrs) {
			console.log("JEG ER HER JO!");
				scope.setPerson(scope.data);
				
				scope.$watch('data', function(newValue, oldValue) {
					if (newValue) {
						scope.setPerson(newValue);
					}


				});

			}
	};
});

/**
 * @ngdoc directive
 * @name dntBookingModule.directive:dntPriceViewer
 * @element div
 * @function
 * @restrict AE
 * @param  {json array} personTypes  json array with supported guest types for cabin
 * @param {Date} fromDate Date the of arrival to cabin
 * @param {Date} toDate Date of departure from cabin
 * @description Displays the total price, and the prices for each guesttype calculated based on booking dates and number of persons in 
 * that category. Its basically a summary component that uses the same personTypes json variable. It recalculates
 * the price whenever dates change or user change any of the select boxes of dntSelector.
 * 
 *
 * **Note:** PersonType should be of the form [{"type": "x", "price": "x", "amount":"x"}, ... , {"type": "x", "price": "x", "amount":"x"}],
 *	AND be the reference the same model as dntSelector to work properly.
 *
 * @example
   	<example module="dntApp">
   		<file name="script.js">
             angular.module('dntApp', ['ui.bootstrap','dntBookingModule'])
             .controller('viewerCtrl', ['$scope',function ($scope) { 
                 $scope.guests = [{"nr": 0,"price": 300,"type": "Voksen, medlem"},{"nr": 0,"price": 150,"type": "Ungdom, medlem"},{"nr": 0,"price": 150,"type": "Ungdom"}];
            		$scope.from = '2014-04-22';
            		$scope.to = '2014-04-23';
            		$scope.iterate = function(number) {
            			$scope.guests[1].nr+=number;
            		};
            		$scope.iterateAnother = function(number) {
            			$scope.guests[2].nr+=number;
            		};
             }]);
         </file>
         <file name="index.html">
   			<div ng-controller="viewerCtrl">
 				
   				<div dnt-price-viewer person-types="guests" from-date="from" to-date="to">
   				</div>
   				
   				<p>===========CONTROLS==============</p>
   				<a ng-click="iterate(1)">pluss one person</a><span> </span><a ng-click="iterateAnother(1)">pluss another one person
   				<input type="text" class="form-control" datepicker-popup ng-model="to"/>
   				<br><br><br><br>
   			</div>
		</file>
 	</example>
 */
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
			'<td style="text-align:right">{{person.nr * person.price * days}} NOK</td>'+
			'</tr>'+
			'</table>'+
			'</div>'+
			'<div class=row>'+
			'<table class="table table-condensed">' +
			'<tr>'+
			'<td><strong>Totalt beløp</strong></td>'+
			'<td style="text-align:right"><p><strong>{{price}} NOK</strong></p></td>' +
			'</tr>'+
			'</table>'+
			'</div></div>',

		controller: ['$scope', '$log', function($scope, $log) {
				$scope.personType = {};
				$scope.days =1;
				

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

			}],

		link: function(scope, elem, attrs) {
				scope.setPersonType(scope.data);
				scope.$watch('data', function(newValue, oldValue) {
					if (newValue) {
						scope.setPersonType(newValue);
					}
					scope.calculatePrice();
				}, true);



				scope.$watch('fromDate', function(newValue, oldValue) {
					if (newValue) {
						scope.newDateRange();
					}

				});

				scope.$watch('toDate', function(newValue, oldValue) {
					if (newValue) {
						scope.newDateRange();
					}

				});
			}
	};
});

/**
 * @ngdoc directive
 * @name dntBookingModule.directive:dntBookingModule
 * @element div
 * @function
 * @restrict AE
 * 
 * @param  {String} cabinType Either 'small' or 'large'. Impact booking module appearance 
 * @param {Number} numberOfBeds Required parameter for large cabins. How many beds can be booked at given large cabin  
 * @param {Json String} bookingModel Json string with some preset parameters is required for this directive. cabinId, guests has to be present.  E.g: {"cabinId":"1","guests":[{"nr":0,"price":300,"type":"Voksen, medlem"},{"nr":0,"price":150,"type":"Ungdom, medlem"}]}. Parameter utilize two way data binding, so parent scope has access to same json string
 * @param {Expression} onBook Binds a parent function to the directive, which will be called when the "reserver" button is pressed
 * @param {String} errorModel Two way binded string. Error messages from the backend can be be set in the parent scope and displayed as red text in the booking module
 * 
 * @description dntBookingModule, contain the different ui elements needed for booking, like dnt-selector and dnt-viewer, a to-from date picker and error display.
 * The directive is responsible for these ui elements, and validation of user input. Any result from user input will be present in
 * the 
 * 
 *
 * **Note:** {"cabinId":"1","dateFrom":"2014-04-19","guests":[{"nr":1,"price":300,"type":"Voksen, medlem"},{"nr":1,"price":150,"type":"Ungdom, medlem"},{"nr":0,"price":100,"type":"Barn, medlem"},{"nr":0,"price":0,"type":"Spedbarn"},{"nr":0,"price":400,"type":"Voksen"},{"nr":0,"price":200,"type":"ungdom"},{"nr":0,"price":150,"type":"barn"}]}
 * @example
   	<example module="dntApp">
   		<file name="script.js">
             angular.module('dntApp', ['ui.bootstrap','dntBookingModule'])
             .controller('selectCtrl', ['$scope',function ($scope) { 
                 $scope.booking = {"cabinId":"1","guests":[{"nr": 0,"price": 300,"type": "Voksen, medlem"},{"nr": 0,"price": 150,"type": "Ungdom, medlem"},{"nr": 0,"price": 150,"type": "Ungdom"}]};
                 $scope.message = "";
                 $scope.called = "";
                 $scope.call = function() {
                 	$scope.called="I've been called because you click the book button";
                 }
             }]);
         </file>
         <file name="index.html">
   			<div ng-controller="selectCtrl">
    			<div class="container">
   				<dnt-booking-module onBook="call()" booking-model="booking" number-of-beds="{{20}}" cabin-type="{{large}}" error-message="message">
   				</dnt-booking-module>
   				<p>{{called}}</p>
   				</div>
  				</div>
			
		</file>
 	</example>
 */
 
angular.module('dntBookingModule')
.directive('dntBookingModule', function() {
	return {
		restrict: 'AE',

		scope: {'cabinType': '@',
			'beds': '@numberOfBeds',
			'booking'		: '=bookingModel',
			'onBook'	: '&',
			'errorMessage' : '=errorModel'
		},

		templateUrl:  'views/bookingComponent.html',

		controller: ['$scope', '$log','$filter', function($scope, $log ,$filter) {
            	$scope.errorMessage;
            	$scope.now = new Date();

            	// Disable weekend selection
            	$scope.disabled = function(date, mode) {
            		return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
            	};
            	
            	//More internal date logic can be put here.
            	/** Track changes from the datepicker calendars and display the from/to dates **/
            	$scope.$watch('booking.dateTo', function(){
            		$scope.booking.dateTo= $filter('date')($scope.booking.dateTo,'yyyy-MM-dd');
            	});

            	
            	$scope.$watch('booking.dateFrom', function(){
            		if ($scope.booking.dateTo < $scope.booking.dateFrom){
            			$scope.booking.dateTo = $scope.booking.dateFrom;
            		}
            		$scope.booking.dateFrom= $filter('date')($scope.booking.dateFrom,'yyyy-MM-dd');
            	});
            	
            	
			}],

		link: function(scope, elem, attrs) {
			//check all variables, validation on input?
				
				
			}
	};
});

//directive that prevents submit if there are still form errors
angular.module('dntBookingModule').directive('validSubmit', [ '$parse', function($parse) {
    return {
        // we need a form controller to be on the same element as this directive
        // in other words: this directive can only be used on a <form>
        require: 'form',
        // one time action per form
        link: function(scope, element, iAttrs, form) {
            form.$submitted = false;
            // get a hold of the function that handles submission when form is valid
            var fn = $parse(iAttrs.validSubmit);

            // register DOM event handler and wire into Angular's lifecycle with scope.$apply
            element.on('submit', function(event) {
                scope.$apply(function() {
                    // on submit event, set submitted to true (like the previous trick)
                    form.$submitted = true;
                    // if form is valid, execute the submission handler function and reset form submission state
                    if (form.$valid) {
                        fn(scope, { $event : event });
                        form.$submitted = false;
                    }
                });
            });
        }
    };
}
]);
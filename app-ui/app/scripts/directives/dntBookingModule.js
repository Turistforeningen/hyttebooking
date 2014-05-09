'use strict';

/**
 * @ngdoc overview
 * @name dntBookingModule
 * @description 
 * # dntBookingModule
 * dntBookingModule contains all the ui elements used for booking, like 
 * {@link dntBookingModule.directive:dntSelector dntSelector} which provide
 *  the selection of attendees for booking, and {@link dntBookingModule.directive:dntPriceViewer dntPriceViewer} showing the total price for 
 * the booking. 
 */
angular.module('dntBookingModule', []);


/**
 * @ngdoc directive
 * @name dntBookingModule.directive:dntSelector
 * @element div
 * @function
 * @restrict AE
 * @param  {json array} categoryModel  json array with supported price categories for cabin
 * @param  {number} dividerIndex  If there are many categories, a number can be used to hide the rest of the categories
 * @param  {number} numberOfBeds  The max bed capacity of the cabin. Prohibits user from selecting more beds than there can actually booked.
 * @description
 * Displays a number of selects decided by the number of categories in `categoryModel`.
 * It will prevent the user from selecting guests from each category such that
 * the total number of guests are over the cabin capacity.
 * 
 *
 * **Note:** `categoryModel` should be of the form [{"type": "x", "price": x, "nr":0, "ageRange": "x"}, ... , {"type": "x", "price": x, "nr":0, "ageRange" : "x"}]
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
 
   				<div dnt-selector category-model="guests" divider-index="hide" number-of-beds="beds">
   				</div>
   				</div>
  				
			
		</file>
 	</example>
 */
angular.module('dntBookingModule', [])
.directive('dntSelector', function() {
	return {
		restrict: 'AE',

		scope: {'categories': '=categoryModel',
			'dividerIndex': '=',
			'beds' : '=numberOfBeds'
		},

		template:
			'<div class="row modBoldText bottom-border"><div class="col-lg-6">Kategori</div><div class="col-lg-2 modCenterText">Pris</div><div class="col-lg-4 modRightText">Antall</div></div>'+
			'<div class="row spaceLineEkstraSmall"></div>'+
			'<div class="row" ng-repeat="per in categories.slice(0, dividerIndex)">'+
			
			'<div class="col-lg-6 col-md-6">'+
			'<p ng-controller="TooltipDemoCtrl" tooltip-placement="top" tooltip-html-unsafe="{{tooltipAge + per.ageRange}}" tooltip-popup-delay="500">{{per.guestType}}</p>'+
			'</div>'+
			
			'<div class="col-lg-2 col-md-2"> <p>{{per.price}}</p>'+
			'</div>'+
			
			'<div class="col-lg-4 col-md-4 modRightText">'+
			'<select class="selectNumber" ng-model="per.nr" ng-options="pe for pe in range(per.nr)"></select>'+
			'</div>'+
			'</div>'+
			
			'<div class="row" ng-show="showDivider">'+
			'<div class="col-lg-12 col-md-12">'+
			'<br>'+
			'<a id="toggle" ng-click="toggleCollapsed()" href="" ng-controller="TooltipDemoCtrl" tooltip-placement="top" tooltip-html-unsafe="{{tooltipNoneMember}}" tooltip-popup-delay="1200">Ikke medlem?</a>'+
			'<br><br>'+
			'<div collapse="isCollapsed">'+
			'<div class="row" ng-repeat="per in categories.slice(dividerIndex)">'+
			'<div class="col-lg-6 col-md-6">'+
			'<p ng-controller="TooltipDemoCtrl" tooltip-placement="top" tooltip-html-unsafe="{{tooltipAge + per.ageRange}}" tooltip-popup-delay="500">{{per.guestType}}</p>'+
			'</div>'+
			
			'<div class="col-lg-2 col-md-2"> <p>{{per.price}}</p>'+
			'</div>'+
			
			'<div class="col-lg-4 col-md-4 modRightText">'+
			'<select class="selectNumber" ng-model="per.nr" ng-options="pe for pe in range(per.nr)"></select>'+
			'</div>'+
			'</div>'+
			'</div>'+

			'</div>'+
			'</div>',

		controller: ['$scope','$log', function($scope,$log) {
				$scope.isCollapsed = true;
				$scope.showDivider = true;
				
				$scope.toggleCollapsed = function() {
					$scope.isCollapsed = !$scope.isCollapsed;
					//When a user shuts down this collapse all entries inside collapse should be erased here
				};
				
				$scope.bedsLeft = function() {
					var left = $scope.beds;
					angular.forEach($scope.categories, function(value, key) {
						left = left -value.nr;
					});
					if(left>0) {
						return left;
					}
					else {
						return 0;
					}
				};
				
				//Called at least once during a digest cycle. Returns appropriate options for pull down
				$scope.range = function(NumberValueChosen) {
					return $scope.ranges[NumberValueChosen];
				};
				
				$scope.ranges = [];
				//constructs all the different ranges/options for the different drop downs
				$scope.constructRange = function() {
					var bedsLeft = $scope.bedsLeft();
					var nrOfBedsChosen = $scope.beds - bedsLeft;
					$scope.$emit('nrOfBedsChosenEvent', nrOfBedsChosen);
					
					angular.forEach($scope.categories, function(value, key){
						var end = bedsLeft;
						if(value.nr !== null || value.nr>0) {
							end = value.nr + bedsLeft;
						}

						var result = [];
						for (var i = 0; i <= end; i++) {
							result.push(i);
						}
						$scope.ranges[value.nr] = result; 
					 });
				}
				
				//Every time there has been a change in the category model (i.e a new number of persons attending), 
				//drop down options has to be updated
				$scope.$watch('categories', function() {
					$scope.constructRange();
					
				}, true);
				
			}],

		link: function(scope, elem, attrs) {				
				scope.$watch('categories', function(newCategories) {
					if (newCategories) {
						if(angular.isUndefined(scope.dividerIndex)) {
							scope.dividerIndex = newCategories.length;
							scope.showDivider = false;
						}
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
 * @param  {json array} categoryModel  json array with supported guest types for cabin
 * @param {Date} fromDate Date the of arrival to cabin
 * @param {Date} toDate Date of departure from cabin
 * @description Displays the total price, and the prices for each guesttype calculated based on booking dates and number of persons in 
 * that category. Its basically a summary component that uses the same personTypes json variable. It recalculates
 * the price whenever dates change or user change any of the select boxes of dntSelector.
 * 
 *
 * **Note:** `categoryModel` should be of the form [{"type": "x", "price": "x", "amount":"x"}, ... , {"type": "x", "price": "x", "amount":"x"}],
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
 				
   				<div dnt-price-viewer category-model="guests" from-date="from" to-date="to">
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

		scope: {'categories': '=categoryModel',
			'fromDate': '=',
			'toDate' : '='


		},

		template:
			'<div class="row" style="min-height: 250px;">' +
			'<table id="prices" class="table table-condensed">' +
			'<tbody>'+
			'<tr ng-repeat="person in categories" ng-show="person.nr>0">'+
			'<td>{{person.guestType}}</td>'+
			'<td>x{{person.nr}}</td>' +
			'<td style="text-align:right">{{person.nr * person.price * days}} NOK</td>'+
			'</tr>'+
			'</tbody>'+
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

		controller: ['$scope', '$log', '$filter', function($scope, $log, $filter) {
				$scope.days =0;
				
				$scope.calculatePrice = function() {
					var totalPrice = 0;
					angular.forEach($scope.categories, function(value, key) {
						totalPrice +=(value.nr * value.price*$scope.days);

					});
					$scope.price = totalPrice;
				};

				$scope.newDateRange = function() {
					if($scope.fromDate  && $scope.toDate) {

						var d1 = new Date($filter('date')($scope.fromDate,'yyyy-MM-dd'));
						var d2 = new Date($filter('date')($scope.toDate,'yyyy-MM-dd'));
						var miliseconds = d2-d1;
						var seconds = miliseconds/1000;
						var minutes = seconds/60;
						var hours = minutes/60;
						var days = hours/24;
						if(days <0 || isNaN(d1.getTime()) || isNaN(d2.getTime()) ) {
							$scope.days = 0;
						}
						else {
							//ceiling will fail if a booking span serveral years.
							//days method not 100% accurate, but close.
							$scope.days = Math.ceil(days);
						}
						$scope.calculatePrice();
					}
				};

			}],

		link: function(scope, elem, attrs) {
				scope.$watch('categories', function() {
					scope.calculatePrice();
				}, true);



				scope.$watch('fromDate', function(newDate) {
					if (newDate) {
						scope.newDateRange();
					}

				}, true);

				scope.$watch('toDate', function(newDate) {
					if (newDate) {
						scope.newDateRange();
					}

				}, true);
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
 * the the bookingModel
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

		scope: {'cabinType'	: '@',
			'beds'			: '@numberOfBeds',
			'booking'		: '=bookingModel',
			'onBook'		: '&',
			'errorMessage'	: '=errorModel',
			'dividerIndex'	: '='
		},

		templateUrl:  'views/bookingComponent.html',

		controller: ['$scope', '$log','$filter', 'bookingService', function($scope, $log ,$filter, bookingService) {
            	$scope.errorMessage;
            	$scope.minimumBookableDate = new Date();
            	//Dates in calendar disabled up to and including today
            	$scope.minimumBookableDate.setDate($scope.minimumBookableDate.getDate() +1);
            	var availability = {};
            	
            	$scope.getAvailability = function(from, to, key) {
            		bookingService.getAvailability($scope.booking.cabinId, from, to)
            		.then(function(data){
            			availability[key] = JSON.parse(data.bookedDays);
            			$scope.$broadcast('date:updateAvailability');
            		});
            	};
            	$scope.$on('date:change', function(event, date) {
            		var year = date.getFullYear(), month = date.getMonth(), firstDayOfMonth = new Date(year, month, 1);
            		var lastDayOfMonth = new Date(year, month+1, 0);
            		var key = year + ' ' + month;
            		$scope.getAvailability($filter('date')(firstDayOfMonth,'yyyy-MM-dd'), $filter('date')(lastDayOfMonth,'yyyy-MM-dd'), key);
            	});  
 
            	var nrOfBedsChosen = 0;
            	$scope.$on('nrOfBedsChosenEvent', function(event, data) {
            		nrOfBedsChosen = data;
            		$scope.$broadcast('date:updateAvailability');
            	}); 
            	
            	//should put a mode scope in dntSelector and put this method and small cabin selector into
            	//that
            	$scope.toggle = function(i) {
            		if(i<=2  && $scope.cabinType == 'small') {
            			if(i == 0) {
            				$scope.booking.guests[0].nr = 1;
            				$scope.booking.guests[1].nr = 0;
            			}
            			else {
            				$scope.booking.guests[0].nr = 0;
            				$scope.booking.guests[1].nr = 1;
            			}
            		}
            	};
            	// Disable weekend selection
            	$scope.disabled = function(date, mode) {
            		var dayOfMonth = date.getDate()-1;
            		var key = date.getFullYear() + ' ' + date.getMonth();
            		if(dayOfMonth >= 0 && availability[key]) {
            			if($scope.beds >0) {
            				//largeCabin
            				if(($scope.beds - availability[key][dayOfMonth] < nrOfBedsChosen) && mode === 'day') {
                				return true;
                			}
            			}
            			else {
            				//smallcabin
            				if((availability[key][dayOfMonth]) && mode === 'day') {
                				return true;
                			}
            			}	
            		}	
            		return false;
            	};
            	
            	//More internal date logic can be put here.
            	/** Track changes from the datepicker calendars and display the from/to dates **/
            	$scope.$watch('booking.dateTo', function(){
            		$scope.booking.dateTo= $filter('date')($scope.booking.dateTo,'yyyy-MM-dd');
            		$scope.errorMessage = null;
            	});

            	$scope.$watch('booking.dateFrom', function(){
            		if ($scope.booking.dateTo < $scope.booking.dateFrom){
            		}
            		$scope.booking.dateFrom= $filter('date')($scope.booking.dateFrom,'yyyy-MM-dd');
            		$scope.errorMessage = null;
            	});
            	
            	$scope.$watch('booking.guests', function(){
            		$scope.errorMessage = null;
            	}, true);
            	
			}],

		link: function(scope, elem, attrs) {
			scope.$broadcast('date:change', new Date());
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
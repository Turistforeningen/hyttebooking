'use strict';

/**
 * @ngdoc directive
 * @name dntBookingModule.directive:dntSelector
 * @element div
 * @function
 * @restrict AE
 * @description
 * Displays a number of selects decided by the number of guestTypes.
 * It will prevent the user from selecting guests from each category such that
 * the total number of guests are over the cabin capacity.
 * 
 *
 * **Note:** PersonType should be of the form [{"type": "x", "price": "x", "amount":"x"}, ... , {"type": "x", "price": "x", "amount":"x"}]
 *
 * @example
   <dntSelector personTypes="[{"type": "ungdom, medlem", "price": "300", "amount":"0"}]" hideTypeIndex="1">
   </example>
 */
angular.module('dntBookingModule', ['ui.bootstrap'])
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
			'<a ng-click="toggleCollapsed()" href="" ng-controller="TooltipDemoCtrl" tooltip-placement="top" tooltip-html-unsafe="{{tooltipNoneMember}}" tooltip-popup-delay="500">Ikke medlem?</a>'+
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

		controller: ['$scope', function($scope) {
				$scope.person = {};
				$scope.isCollapsed = true;

				$scope.toggleCollapsed = function() {
					$scope.isCollapsed = !$scope.isCollapsed;
					//When a user shuts down this collapse all entries inside collapse should be erased here
				};
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
 * @description
 * Displays the total price, and the prices for each guesttype calculated based on booking dates and number of persons in 
 * that category. Its basically a summary component that uses the same personTypes json variable. It recalculates
 * the price whenever dates change or user change any of the select boxes of dntSelector.
 * 
 *
 * **Note:** PersonType should be of the form [{"type": "x", "price": "x", "amount":"x"}, ... , {"type": "x", "price": "x", "amount":"x"}],
 *	AND be the reference the same model as dntSelector to work properly.
 *
 * @example
   <dntPriceViewers personTypes="[{"type": "ungdom, medlem", "price": "300", "amount":"0"}]" hideTypeIndex="1">
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
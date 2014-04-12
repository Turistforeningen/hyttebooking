'use strict';


angular.module('dntApp').controller( 'ModalInstanceCtrl', ['$scope','$rootScope','$modalInstance','$log','item',
                         function ($scope,$rootScope, $modalInstance,$log, item) {

	$scope.booking = item;
	$scope.selected = {
			item: $scope.booking
		};

	$scope.ok = function () {
		$log.info("lol");
		$rootScope.$broadcast('event:booking');
		$modalInstance.close($scope.selected.item);
	};

	$scope.cancel = function () {
		$log.info('dismiss');
		$modalInstance.dismiss('cancel');
	};
}]);

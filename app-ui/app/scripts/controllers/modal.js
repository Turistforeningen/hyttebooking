'use strict';


angular.module('dntApp').controller( 'ModalInstanceCtrl', ['$scope','$modalInstance','$log','item',
                         function ($scope, $modalInstance,$log, item) {
	$scope.data = item;
	$scope.selected = {
			item: $scope.data
		};

	$scope.ok = function () {
		$modalInstance.close();
	};

	$scope.cancel = function () {
		$log.info('dismiss');
		$modalInstance.dismiss('cancel');
	};
}]);

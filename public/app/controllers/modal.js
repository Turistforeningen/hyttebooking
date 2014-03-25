angular.module('plunker', ['ui.bootstrap']);
var ModalController = function ($rootScope, $scope, $modal, $log) {

  $scope.items = ['item1', 'item2', 'item3'];

  $scope.open = function (url) {
	  
	  

    var modalInstance = $modal.open({
      templateUrl: url,
      controller: ModalInstanceCtrl,
      resolve: {
        items: function () {
          return $scope.items;
        }
      }
    });

    modalInstance.result.then(function (selectedItem) {
      $scope.selected = selectedItem;
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };
  
  $scope.$watch('paid', function() {
	  if($scope.paid !=0) {
		  if($scope.paid ==1) {
			  $scope.checked = true;  
		  }
		  else {
			   $scope.checked = false;   
		  }
		  $scope.open('/assets/app/partials/statusModal.html');
	  }
	  
  });

};

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $modal service used above.

var ModalInstanceCtrl = function ($scope,$rootScope, $modalInstance,$log, items) {

  $scope.items = items;
  $scope.selected = {
    item: $scope.items[0]
  };

  $scope.ok = function () {
	 $rootScope.$broadcast('event:booking');
    $modalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
};
angular.module('plunker', ['ui.bootstrap']);
var TooltipDemoCtrl = function ($scope) {
	  $scope.dynamicTooltip = "Hello, World!";
	  $scope.dynamicTooltipText = "dynamic";
	  $scope.htmlTooltip = "I've been made <b>bold</b>!";
	  
	  
	  $('.tooltip-demo').tooltip({
		  selector: "a[rel=tooltip]"
		})
};



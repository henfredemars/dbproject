var app = angular.module('app',[]);

app.controller('adderController', function($scope) {
	$scope.firstNumber = 2;
	$scope.secondNumber = 2;
	$scope.lvalue = 2+2;
	$scope.thirdNumber = function () {
		if (!$scope.firstNumber || !$scope.secondNumber) {
			return $scope.lvalue;
		} else {
		$scope.lvalue=parseInt($scope.firstNumber)+
			parseInt($scope.secondNumber);
			return $scope.lvalue;
		}
	};
});



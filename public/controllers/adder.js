var app = angular.module('app',[]);

app.controller('adderController', function($scope) {
	$scope.firstNumber = 2;
	$scope.secondNumber = 2;
	$scope.thirdNumber = function () {
		return parseInt($scope.firstNumber)+
			parseInt($scope.secondNumber);
	};
});



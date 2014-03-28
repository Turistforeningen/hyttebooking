angular.module('plunker', ['ui.bootstrap']);
var TooltipDemoCtrl = function ($scope) {
	$scope.tooltipBookingInfo = '<p align="left">Bookingen foregår over tre steg fra venstre mot høyre;</p><p align="left">1. Velg antall personer i kategorier bookingen gjelder for.<br>2. Velg dato for ankomst og avreise.<br>3. Trykk på Reserver for å gå videre til bekreftelse av reservasjon.</p><p align="left">Du finner en online brukermanual her!</p>'
	$scope.tooltipNoneMember = '<p>Dersom du ikke er medlem av DNT, så får du<br>fram valg for ikke-medlemskap ved å trykke her.</p>'
	$scope.tooltipArrival = '<p>Velg dato for ankomst.</p>'
	$scope.tooltipDeparture = '<p>Velg dato for avreise.</p>'
	$scope.tooltipReserveButton = '<p>Neste steg er ekreftelse av reservasjon. Klikk her.</p>'
};
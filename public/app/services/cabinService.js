app.service('cabinService', function ($http, $log) {
    
    
    this.getCabins = function (page, pageSize) {
        var url = '/api/cabins?page=' + page + "&size=" + pageSize;
        return $http.get(url); 
    };
    
    this.getCabinDetails = function (page, pageSize, id) {
        var url = '/api/cabins/' + id+'?page=' + page + "&size=" + pageSize;
        return $http.get(url);  
    };

});
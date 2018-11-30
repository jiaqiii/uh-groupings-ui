(function () {

    /**
     * Controller for the memberships page.
     * @param $scope - binding between controller and HTML page
     * @param $window - the browser window object
     * @param $controller - the service for instantiating controllers
     * @param dataProvider - the service that provides GET and POST requests for getting or updating data
     * @param BASE_URL - the constant base URL for endpoints
     */
    function MembershipJsController($scope, $window, $controller, dataProvider, BASE_URL) {

        $scope.membershipsList = [];
        $scope.pagedItemsMemberships = [];
        $scope.currentPageMemberships = 0;

        $scope.optInList = [];
        $scope.pagedItemsOptInList = [];
        $scope.currentPageOptIn = 0;

        $scope.loading = true;

        angular.extend(this, $controller("TableJsController", { $scope: $scope }));
        angular.extend(this, $controller("TimeoutJsController", { $scope: $scope }));

        /**
         * Loads the groups the user is a member in, the groups the user is able to opt in to, and the groups the user
         * is able to opt out of.
         */
        $scope.init = function () {
            var endpoint = BASE_URL + "groupingAssignment";

            dataProvider.loadData(function (res) {
                $scope.membershipsList = _.sortBy(res.groupingsIn, "name");
                $scope.filter($scope.membershipsList, "pagedItemsMemberships", "currentPageMemberships", $scope.membersQuery);

                $scope.optInList = _.sortBy(res.groupingsToOptInTo, "name");
                $scope.filter($scope.optInList, "pagedItemsOptInList", "currentPageOptIn", $scope.optInQuery);

                $scope.loading = false;
            }, function (res) {
                dataProvider.handleException({ exceptionMessage: res.exceptionMessage }, "feedback/error", "feedback");
            }, endpoint);
        };

        /**
         * Handles requests for opting in to or out of a grouping.
         * @param {string} endpoint - the API endpoint to opt in/out of a grouping
         */
        function handleOptRequest(endpoint) {
            $scope.loading = true;
            dataProvider.updateData(function (res) {
                if (_.startsWith(res[0].resultCode, "FAILURE")) {
                    alert("Failed to opt out");
                    $scope.loading = false;
                } else {
                    $scope.init();
                }
            }, function (res) {
                console.log("Error, Status Code: " + res.statusCode);
            }, endpoint);
        }

        /**
         * Adds the user to the exclude group of the grouping selected. Sends back an alert saying if it failed.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optOut = function (currentPage, indexClicked) {
            var groupingPath = $scope.pagedItemsMemberships[currentPage][indexClicked].path;
            var endpoint = BASE_URL + groupingPath + "/optOut";
            handleOptRequest(endpoint);
        };

        /**
         * Adds the user to the include group of the grouping selected.
         * @param {number} currentPage - the current page within the table
         * @param {number} indexClicked - the index of the grouping clicked by the user
         */
        $scope.optIn = function (currentPage, indexClicked) {
            var groupingPath = $scope.pagedItemsOptInList[currentPage][indexClicked].path;
            var endpoint = BASE_URL + groupingPath + "/optIn";
            handleOptRequest(endpoint);
        };

    }

    UHGroupingsApp.controller("MembershipJsController", MembershipJsController);

})();

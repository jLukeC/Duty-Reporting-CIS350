var oneHour = 1000 * 60 * 60;
var oneDay = oneHour * 24;

Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define('averageLengthBetweenDayOff', function(request, response) {
    var query = new Parse.Query('HourEntry');
    console.log(request.params.username);
    query.equalTo('username', request.params.username);

    var monthAgoDate = new Date(Date.now() - (27 * oneDay));
    monthAgoDate.setHours(0);
    monthAgoDate.setMinutes(0);
    monthAgoDate.setSeconds(0);
    monthAgoDate.setMilliseconds(0);
    query.greaterThan('startTime', monthAgoDate);
    query.ascending('date');

    query.find({
        success: function(results) {

            var daysOff = 0;
            for (var i = 0; i < 28; i++) {
                var currentDay = new Date(Date.now() - (i * oneDay));
                var workedDay = false;
                
                for (var j = 0; j < results.length; j++) {
                    if (sameDate(results[j].get('startTime'), currentDay)) {
                        workedDay = true;
                        break;
                    }
                }
                if (!workedDay) {
                    daysOff++;
                }
            }
            response.success(28/daysOff);
        },
        error: function(error) {
            response.error('Retreiving user data failed');
        }
    });

});


// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define('dutyViolations', function(request, response) {
  var query = new Parse.Query('HourEntry');
  query.equalTo('username', request.params.username);
  //Query dates must be limited to a month ago

  var monthAgoDate = new Date(Date.now() - (27 * oneDay));
  monthAgoDate.setHours(0);
  monthAgoDate.setMinutes(0);
  monthAgoDate.setSeconds(0);
  monthAgoDate.setMilliseconds(0);
  query.greaterThan('startTime', monthAgoDate);
  query.ascending('date');


  //results is an array of all queries that fit the specified parameters
  query.find({
  	success: function(results) {

        var violations = {};

  		//Check for 80 hrs weekly over past 4 weeks
  		var totalHours = 0;

  		for (var i = 0; i < results.length; i++) {
  			if (typeof results[i] === 'undefined') {
  				continue;
  			}
  			totalHours += results[i].get('hours');
  		}
        console.log('the total number of hours in the last month is: ' + totalHours);
  		if (totalHours/4 > 80) {
  			violations.monthHourViolation = true;
  		} else {
  			violations.monthHourViolation = false;
  		}

  		//Check for 1 day off in each of the previous weeks
        //Check to see how many days in the past week were worked on
        violations.weekHourViolation = [];

        for (var week = 0; week < 4; week++) {
            var daysWorkedLastWeek = 0;
            for (var i = 0; i < 7; i++) {
                var tempDateCheck = new Date(Date.now() - (i * oneDay) - (week * 7 * oneDay));
                
                //Once we know the date to check, cycle through the query response to see if any date corresponds to the current one
                var workedOnDate = false;
                for (var j = 0; j < results.length; j++) {
                    if (sameDate(tempDateCheck, new Date(results[j].get('startTime')))) {
                        workedOnDate = true;
                        break;
                    }
                }
                if (workedOnDate) {
                    daysWorkedLastWeek++;
                }
            }

            if (daysWorkedLastWeek === 7) {
            violations.weekHourViolation.push(new Date(Date.now() - (week * 7 * oneDay)));
            }
        }

  		
        
		//Check for at least eight hours off
        violations.restPeriodViolation = [];

        for (var i = results.length - 2; i >= 0; i--) {
            var prevEndTime = new Date(results[i + 1].get('startTime').getTime() 
                + (oneHour * results[i + 1].get('hours') + 8));
            if ((results[i].get('startTime').getTime() - prevEndTime.getTime()) < 0) {
                violations.restPeriodViolation.push(results[i].get('startTime'));
            } 
        }


        //Check for 24 + 4 violations
        violations.shiftViolations = [];
        for (var i = 0; i < results.length; i++) {
            if (typeof results[i] === 'undefined') {
                continue;
            }
            if (results[i].get('hours') > 28) {
                violations.shiftViolations.push(results[i].get('startTime'));
            }
        }

		
        response.success(JSON.stringify(violations));
  	},
  	error: function(error) {
  		response.error('Retreiving user data failed');
  	}
  });
});

var sameDate = function (date1, date2) {
	return (date1.getDate() === date2.getDate() 
			&& date1.getMonth() === date2.getMonth()
			&& date1.getFullYear() === date2.getFullYear());
};

Parse.Cloud

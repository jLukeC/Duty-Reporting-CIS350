var oneHour = 1000 * 60 * 60;
var oneDay = oneHour * 24;

Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
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

  		if (totalHours/4 > 80) {
  			violations.monthHourViolation = 2;
  		} else if (totalHours/4 > 75) {
  			violations.monthHourViolation = 1;
  		} else {
  			violations.monthHourViolation = 0;
  		}

  		//Check for 1 day off in previous week
        //Check to see how many days in the past week were worked on
        var daysWorkedLastWeek = 0;
        for (var i = 0; i < 7; i++) {
            var tempDateCheck = new Date(Date.now() - (i * oneDay));
            
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
  			violations.weekHourViolation = 2;
  		} else if (daysWorkedLastWeek === 6) {
            violations.weekHourViolation = 1;
        } else {
            violations.weekHourViolation = 0;
        }
        
		//Check for at least eight hours off
        var startDate = new Date(results[0].get('startTime'));
        var endTime = startDate.getTime() + (oneHour * results[0].get('hours'));
		var hoursElapsed = (Date.now() - endTime)/oneHour;
		if (typeof results[0] === 'undefined') {
			violations.restPeriodViolation = 0;
		} else if (hoursElapsed < 8) {
			violations.restPeriodViolation = 2;
		} else if (hoursElapsed < 10) {
			violations.restPeriodViolation = 1;
		} else {
			violations.restPeriodViolation = 0;
		}
		
        response.success(violations);
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

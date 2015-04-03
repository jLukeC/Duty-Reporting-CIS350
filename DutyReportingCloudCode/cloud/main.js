
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define('dutyViolations', function(request, response) {
  var query = new Parse.query('HourEntry');
  query.equalTo('username', request.params.username);
  query.limit(28);
  query.ascending('date');


  //results is an array of all queries that fit the specified parameters
  query.find({
  	success: function(results) {
  		var oneHour = 1000 * 60 * 60;
  		var oneDay = oneHour * 24;

  		//Check for 80 hrs weekly over past 4 weeks
  		var totalHours = 0;

  		for (var i = 0; i < results.length; i++) {
  			if (typeof results[i] === 'undefined') {
  				break;
  			}
  			totalHours += (results[i].endTime.getTime() - results[i].startTime.getTime())/oneHour;
  		}
  		if (totalHours/4 > 80) {
  			response.monthHourViolation = 2;
  		} else if (totalHours/4 > 75) {
  			response.monthHourViolation = 1;
  		} else {
  			response.monthHourViolation = 0;
  		}

  		//Check for 1 day off in previous week
		var currentDate = new Date();
		var tempDate = new Date(Date.now() - (7 * oneDay));

		if (typeof results[6] !== 'undefined' && sameDate(tempDate, results[6].startTime)) {
			response.weekHourViolation = 2;
		} else {
			tempDate = new Date(tempDate.getTime() + oneDay);

			if (typeof results[5] !== 'undefined' && sameDate(tempDate, results[5].startTime)) {
				response.weekHourViolation = 1;
			} else {
				response.weekHourViolation = 0;
			}
		}

		//Check for at least eight hours off
		var hoursElapsed = (currentDate.getTime() - results[0].endTime.getTime())/oneHour;

		if (typeof results[0] === 'undefined') {
			response.restPeriodViolation = 0;
		} else if (hoursElapsed < 8) {
			response.restPeriodViolation = 2;
		} else if (hoursElapsed < 10) {
			response.restPeriodViolation = 1;
		} else {
			response.restPeriodViolation = 0;
		}
		response.success();
  	},
  	error: function(error) {
  		response.error();
  	}
  });
});

var sameDate = function (date1, date2) {
	return (date1.getDate() === date2.getDate() 
			&& date1.getMonth() === date2.getMonth()
			&& date1.getFullYear() === date2.getFullYear());
};

Parse.Cloud

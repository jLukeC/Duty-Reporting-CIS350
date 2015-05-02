var message = require('../cloud/message.js');
var expect = require("expect.js");

describe('Message', function () {
  describe('response', function () {
      it('should return the correct message', function (done) {
  		message.getMessage({}, {
      		success : function (message) {
          		done();
          		expect(message).to.be.eql("Good job, buddy");
      		}
  		});
	});
  });
});
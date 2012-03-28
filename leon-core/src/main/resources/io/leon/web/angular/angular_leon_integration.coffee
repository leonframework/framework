leonServiceModule = angular.module('leon.service', ['ng'])
    
leonServiceModule.service "leon", ($rootScope) ->
	leon = {}
	leon.__proto__ = getLeon()
	leon.service = (url, methodName) ->
		call: (args...) ->
			refreshHook = () ->
				#$rootScope.$digest()
			getLeon().service(url, methodName, refreshHook).call.apply(this, args)

	leon.subscribeTopic = (topicId, handler) ->
		getLeon().comet.subscribeTopic topicId, (data) ->
			handler(data)
			#$rootScope.$digest()

	leon

leonModule = angular.module('leon', ['ng', 'leon.service'])

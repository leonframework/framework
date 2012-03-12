
angular.service "leon", ($updateView) ->
	leon = {}
	leon.__proto__ = getLeon()
	leon.service = (url, methodName) ->
		call: (args...) ->
			refreshHook = () ->
				$updateView()
			getLeon().service(url, methodName, refreshHook).call.apply(this, args)

	leon.subscribeTopic = (topicId, handler) ->
		getLeon().comet.subscribeTopic topicId, (data) ->
			handler(data)
			$updateView()

	leon

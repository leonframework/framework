# ----------
# In this step of the leon angular integration we provide some defaults which can be used to start quickly with a new
# application
# ----------



# local alias which an be used as clojure to bypass this/@
leonAngular = @getLeon().angular



###
A simple module including angular and leon.
It's ready to use to implement small applications which do need an own module.
###
leonAngular.leonAppModule = angular.module 'leonApp', ['ng', 'leon.core']



###
A simple module including angular, leon and leon's crud support.
It's ready to use to implement small applications which do need an own module.
###
leonAngular.leonAppCrudModule = angular.module 'leonCrudApp', ['ng', 'leon.core', 'leon.crud']

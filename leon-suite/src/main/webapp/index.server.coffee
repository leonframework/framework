
package = Packages.com.acme.appname
serviceA = -> leon.inject(package.ServiceA)
serviceB = -> leon.inject(package.ServiceB)

@indexService =
	serviceA: ->
		serviceA().getA()

	serviceB: ->
		serviceB().getB()

	serviceAB: ->
		a: serviceA().getA()
		b: serviceB().getB()


var savePerson = function (person) {
    println("JS server: " + person);
    println("JS server: " + person.address.zipcode)
    var zc = person.address.zipcode

    com.ww.sjs.SJSAtmosphereQueueHandler.test()

    return {
        a: 1,
        b: zc * 3
    };
}

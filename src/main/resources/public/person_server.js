

function savePerson(person) {
    println("JS server: " + person);
    println("JS server: " + person.address.zipcode)
    var zc = person.address.zipcode

    return {
        a: 1,
        b: zc * 3
    };
}


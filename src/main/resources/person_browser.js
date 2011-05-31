
function extractPerson() {
    var person = ko.toJS(viewModel);

    invoke("savePerson", [person], function(result) {
        console.log("result:" + result);
    });
}





app.filter('formatResources', function() {
	return spaceThousand.compose(kmg)
})

//add K, G or M for lisibility
function kmg(input) {
	var suffixes = ["", " K", " M", " G"]
	var num = input
	var i = 0
	while (Math.floor(num / 1000) > 1000 && i < (suffixes.length - 1)) {
		num = Math.ceil(num / 1000)
		i++
	}
	return num + suffixes[i]
}

function spaceThousand(input) {
	return input != undefined ? input.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") : input;
}

app.filter('firstLetter', function() {
	return function(input) {
		return (!!input) ? input.charAt(0).toUpperCase() : '';
	}
})
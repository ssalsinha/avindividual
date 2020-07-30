$(function(){
    $("#registerBtn").on("click", function(){
        var user = {
			name: $("#name").val(),
			password: $("#password").val(),
			confirmation: $("#confirmation").val(),
			address: $("#address").val(),
			phoneNR: $("#phone").val(),
		};
		if(user.password != user.confirmation || user.name == "" || user.password == ""){
			alert("Invalid name/password!")
			return;
		}
        $.ajax({
			contentType: 'application/json;charset=UTF-8',
			type: "POST",
			url: "https://aindi-276521.appspot.com/rest/register",
			data: JSON.stringify(user),
			success: function(token){
				Cookies.set("token",JSON.stringify(token));
                alert("register successful");
				location.href = "home.html"
			},
			error: function(){
				alert("Error!!");
			}
		});
    });
});
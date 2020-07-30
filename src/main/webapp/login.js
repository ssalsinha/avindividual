$(function(){
	$("#loginBtn").on("click", function() {
        var user = {
			name: $("#name").val(),
			password: $("#password").val()
		};
        
        $.ajax({
			contentType: 'application/json;charset=UTF-8',
			type: "POST",
			url: "https://aindi-276521.appspot.com/rest/login",
			data: JSON.stringify(user),
			success: function(token){
                Cookies.set("token",JSON.stringify(token));
                alert("login successful");
                location.href = "home.html"
                
			},
			error: function(){
				alert("Wrong name/password credentials!");
			}
		});
		
    });
});
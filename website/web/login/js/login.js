function login() {

    var user = document.getElementById('email').value;
    console.log(user);
    var pass = document.getElementById('pass').value;
    console.log(pass);
    var req = new XMLHttpRequest();

    req.open("POST", "http://localhost:8080/archery/auth/signIn", false);
    req.setRequestHeader('Content-Type', 'application/json');
    req.withCredentials = true;
    req.onload = function () {
        if (req.status !== 200) {
            alert("Wrong email or password, please try again");
        } else {
            console.log("Success");
            document.location.href = "../lk.html";
        }
    }
    req.send(JSON.stringify({login: user, password: pass}));
}
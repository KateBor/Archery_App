function saveToken(token) {
    localStorage.setItem('tokenData', JSON.stringify(token));
}

function refreshToken(token) {
    return fetch('http://localhost:8080/archery/auth/refreshToken', {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({token}),
    })
        .then((res) => {
            if (res.status === 200) {
                const tokenData = res.json(); //не уверена что так
                saveToken(JSON.stringify(tokenData));
                return Promise.resolve();
            }
            return Promise.reject();
        });
}

async function fetchWithAuth(url, options) {
    const loginUrl = '../../login/login.html';
    var tokenData_t = null;
    var access_t = null;
    var expires = null;
    if (localStorage.tokenData) {
        // console.log("token exist");
        tokenData_t = localStorage.tokenData;
        // console.log(localStorage.tokenData);
        access_t = JSON.parse(localStorage.getItem('tokenData'));
        // console.log(access_t);
        expires = JSON.parse(localStorage.getItem('expiresIn'));
        // console.log(expires);

    } else {
        console.log("localStorage.authToken = null");
        return window.location.replace(loginUrl);
    }

    if (!options.headers) {
        console.log("there aren't headers");
        options.headers = {}; // Нужно добавить необходимые заголовки
    }

    if (tokenData_t) {
        if (Date.now() >= expires) {
            console.log("token isn't valid");
            try {
                const newToken = await refreshToken(access_t);
                saveToken(newToken);
            } catch (err) {
                return window.location.replace(loginUrl);
            }
        }
        options.headers.Authorization = `Bearer ${access_t}`;
    }
    return fetch(url, options);
}

function save_changes(selectedOption) {
    var value = selectedOption.value;
    console.log(value);
}

function new_users() {
    var tbody = document.getElementById("new_users_table").getElementsByTagName('tbody')[0];
    var row = document.createElement("tr")
    var td_name = document.createElement("td")
    let a = document.createElement("a");
    row.appendChild(td_name);
    let div_name = document.createElement("div");
    div_name.className = "widget-26-job-title";
    if (localStorage.getItem("last_id") == myarr_global.length) {
        a.textContent = "Новый пользователей пока нет";
    } else {
        for (let i = localStorage.getItem("last_id"); i < myarr_global.length; ++i) {

            a.onclick = function () {
                localStorage.setItem("user_id", myarr_global[i].id);
                a.href = "./../../users_lk_for_admin/users_lk_for_admin.html";
            };
            a.textContent = myarr_global[i].first_name + " " + myarr_global[i].last_name;
        }
    }
    td_name.appendChild(div_name);
    div_name.appendChild(a);
    localStorage.setItem("last_id", myarr_global.length);
    tbody.appendChild(row);
}

function add_name(row, name, id) {
    var td_name = document.createElement("td")
    row.appendChild(td_name);
    let div_name = document.createElement("div");
    div_name.className = "widget-26-job-title";
    td_name.appendChild(div_name);
    let a = document.createElement("a");
    a.onclick = function () {
        localStorage.setItem("user_id", id);
        a.href = "./../../users_lk_for_admin/users_lk_for_admin.html";
    };
    a.textContent = name;
    div_name.appendChild(a);
}

function add_email(row, email) {
    var td2 = document.createElement("td")
    row.appendChild(td2);
    let div2 = document.createElement("div");
    div2.className = "widget-26-job-title";
    td2.appendChild(div2);
    let p1 = document.createElement("p");
    p1.className = "type m-0";
    p1.textContent = email;
    div2.appendChild(p1);
}

function add_phone(row, phone) {
    var td3 = document.createElement("td")
    row.appendChild(td3);
    let div3 = document.createElement("div");
    div3.className = "widget-26-job-title";
    td3.appendChild(div3);
    let p2 = document.createElement("p");
    p2.className = "type m-0";
    p2.textContent = phone;
    div3.appendChild(p2);
}

function add_birth_date(row, birth_date) {
    var td4 = document.createElement("td")
    row.appendChild(td4);
    let div4 = document.createElement("div");
    div4.className = "widget-26-job-title";
    td4.appendChild(div4);
    let p3 = document.createElement("p");
    p3.className = "type m-0";
    p3.textContent = birth_date.slice(0, 10);
    div4.appendChild(p3);
}

function add_ranks(row, id, rank) {
    var ranks = ["middles", "juniors", "seniors", "no rank"];
    var td_rank = document.createElement("td")
    row.appendChild(td_rank);
    let div_rank = document.createElement("div");
    div_rank.className = "widget-26-job-title";
    let span_rank = document.createElement("span");
    let select_rank = document.createElement("select");
    select_rank.id = `sel_rank + ${id}`;
    select_rank.className = "form-control category-select";
    select_rank.onchange = function () {
        let url = "http://localhost:8080/archery/admin/setRankAndApprove";
        var data = {
            id: id,
            rank: document.getElementById(`sel_rank + ${id}`).value
        };
        fetchWithAuth(url,
            {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data),
            }).then((res) => {
            if (res.status === 200) {
                console.log("saving...");
            } else {
                window.alert("fail");
            }
        });
    };
    for (var i = 0; i < ranks.length; i++) {
        var option_rank = document.createElement("option");
        option_rank.value = ranks[i];
        option_rank.text = ranks[i];
        select_rank.appendChild(option_rank);
    }

    if (rank != null) {
        if (rank.rank_name == ranks[0]) {
            select_rank.className = "widget-26-job-category bg-soft-warning";
            select_rank[0].selected = true;
        } else if (rank.rank_name == ranks[1]) {
            select_rank.className = "widget-26-job-category bg-soft-success";
            select_rank[1].selected = true;
        } else if (rank.rank_name == ranks[2]) {
            select_rank.className = "widget-26-job-category bg-soft-danger";
            select_rank[2].selected = true;
        } else if (rank.rank_name == ranks[3]) {
            select_rank.className = "widget-26-job-category bg-soft-secondary";
            select_rank[3].selected = true;
        }
        span_rank.textContent = rank.rank_name;
    } else {
        select_rank.className = "widget-26-job-category bg-soft-secondary";
        span_rank.textContent = ranks[3];
        select_rank[3].selected = true;
    }

    div_rank.appendChild(select_rank);
    td_rank.appendChild(div_rank);
}

function add_ticket(row, id) {
    var passes = ["unlimited", "long limit", "short limit", "no ticket"];
    var passes_rus = ["Безлимитный", "8 занятий", "4 занятия", "Нет"];
    var td_pass = document.createElement("td")
    row.appendChild(td_pass);
    let div_pass = document.createElement("div");
    div_pass.className = "widget-26-job-title";
    let span_pass = document.createElement("span");
    let select_pass = document.createElement("select");
    select_pass.id = `sel_pass + ${id}`;
    select_pass.className = "form-control category-select";
    select_pass.onchange = function () {
        let url = "http://localhost:8080/archery/admin/students/changeticket";
        var data = {
            id: id,
            seasonTicketType: document.getElementById(`sel_pass + ${id}`).value
        };
        fetchWithAuth(url,
            {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data),
            }).then((res) => {
            if (res.status === 200) {
                console.log("saving...");
            } else {
                window.alert("fail");
            }
        });
    };
    for (i = 0; i < passes.length; i++) {
        var option_pass = document.createElement("option");
        option_pass.value = passes[i];
        option_pass.text = passes_rus[i];
        select_pass.appendChild(option_pass);
    }
    let pass;
    fetchWithAuth(`http://localhost:8080/archery/admin/students/ticket/${id}`,
        {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(response => {
        return response.json();
    }).then(data => {
        pass = data.ticketType;
        return pass;
    }).then(pass => {
        if (pass != "") {
            if (pass == passes[0]) {
                select_pass.className = "widget-26-job-category bg-soft-success";
                select_pass[0].selected = true;
                span_pass.textContent = "Безлимитный";
            } else if (pass == passes[1]) {
                select_pass.className = "widget-26-job-category bg-soft-warning";
                select_pass[1].selected = true;
                span_pass.textContent = "8 занятий";
            } else if (pass == passes[2]) {
                select_pass.className = "widget-26-job-category bg-soft-danger";
                select_pass[2].selected = true;
                span_pass.textContent = "4 занятия";
            } else if (pass == passes[3]) {
                select_pass.className = "widget-26-job-category bg-soft-secondary";
                select_pass[3].selected = true;
                span_pass.textContent = "Нет";
            }
        } else {
            select_pass.className = "widget-26-job-category bg-soft-secondary";
            select_pass[3].selected = true;
            span_pass.textContent = "Нет";
        }
    }).catch(err => {
        console.log(err);
    });

    div_pass.appendChild(select_pass);
    td_pass.appendChild(div_pass);
}

function addRow(id, user) {

    var tbody = document.getElementById(id).getElementsByTagName('tbody')[0];
    var row = document.createElement("tr")

    // name
    add_name(row, user.first_name + " " + user.last_name, user.id);

    // email
    // add_email(row,user.email);

    // phone
    add_phone(row, user.phone_number);

    // birth date
    // add_birth_date(row,user.birth_date);

    // rank
    add_ranks(row, user.id, user.rank_name);

    // абонемент
    add_ticket(row, user.id);

    tbody.appendChild(row);
}

function print(array, start, end) {
    for (let j = start; j < end; j++) {
        addRow('users', array[j]);
    }
}

// on page load collect data to load pagination as well as table
// const data = {"req_per_page": document.getElementById("req_per_page").value, "page_no": 1};
const data = {"req_per_page": 'ALL', "page_no": 1};

// At a time maximum allowed pages to be shown in pagination div
const pagination_visible_pages = 4;

// hide pages from pagination from beginning if more than pagination_visible_pages
function hide_from_beginning(element) {
    if (element.style.display === "" || element.style.display === "block") {
        element.style.display = "none";
    } else {
        hide_from_beginning(element.nextSibling);
    }
}

// hide pages from pagination ending if more than pagination_visible_pages
function hide_from_end(element) {
    if (element.style.display === "" || element.style.display === "block") {
        element.style.display = "none";
    } else {
        hide_from_beginning(element.previousSibling);
    }
}

// load data and style for active page
function active_page(element, rows, req_per_page) {
    var current_page = document.getElementsByClassName('active');
    var next_link = document.getElementById('next_link');
    var prev_link = document.getElementById('prev_link');
    var next_tab = current_page[0].nextSibling;
    var prev_tab = current_page[0].previousSibling;
    current_page[0].className = current_page[0].className.replace("active", "");
    if (element === "next") {
        if (parseInt(next_tab.text).toString() === 'NaN') {
            next_tab.previousSibling.className += " active";
            next_tab.setAttribute("onclick", "return false");
        } else {
            next_tab.className += " active"
            render_table_rows(rows, parseInt(req_per_page), parseInt(next_tab.text));
            if (prev_link.getAttribute("onclick") === "return false") {
                prev_link.setAttribute("onclick", `active_page('prev',\"${rows}\",${req_per_page})`);
            }
            if (next_tab.style.display === "none") {
                next_tab.style.display = "block";
                hide_from_beginning(prev_link.nextSibling)
            }
        }
    } else if (element === "prev") {
        if (parseInt(prev_tab.text).toString() === 'NaN') {
            prev_tab.nextSibling.className += " active";
            prev_tab.setAttribute("onclick", "return false");
        } else {
            prev_tab.className += " active";
            render_table_rows(rows, parseInt(req_per_page), parseInt(prev_tab.text));
            if (next_link.getAttribute("onclick") === "return false") {
                next_link.setAttribute("onclick", `active_page('next',\"${rows}\",${req_per_page})`);
            }
            if (prev_tab.style.display === "none") {
                prev_tab.style.display = "block";
                hide_from_end(next_link.previousSibling)
            }
        }
    } else {
        element.className += "active";
        render_table_rows(rows, parseInt(req_per_page), parseInt(element.text));
        if (prev_link.getAttribute("onclick") === "return false") {
            prev_link.setAttribute("onclick", `active_page('prev',\"${rows}\",${req_per_page})`);
        }
        if (next_link.getAttribute("onclick") === "return false") {
            next_link.setAttribute("onclick", `active_page('next',\"${rows}\",${req_per_page})`);
        }
    }
}

// Render the table's row in table request-table
function render_table_rows(rows, req_per_page, page_no, option_all) {
    const response = JSON.parse(window.atob(rows));
    let resp;
    if (option_all !== 'ALL') {
        resp = response.slice(req_per_page * (page_no - 1), req_per_page * page_no);
    } else {
        resp = response.slice(0, rows.length);
    }
    $("#users > tbody").html("");
    $('#users').append('<tr><th>Name</th><th>Phone</th><th>Rank</th><th>Ticket</th></tr>');
    resp.forEach(function (element) {
        addRow('users', element);
    });
}

// Pagination logic implementation
function pagination(data, myarr, length) {
    const all_data = window.btoa(JSON.stringify(myarr));
    $(".pagination").empty();

    if (data.req_per_page !== 'ALL') {
        let pager = `<a href="#" id="prev_link" onclick=active_page('prev',\"${all_data}\",${data.req_per_page})>&laquo;</a>` +
            `<a href="#" class="active" onclick=active_page(this,\"${all_data}\",${data.req_per_page})>1</a>`;
        const total_page = Math.ceil(parseInt(length) / parseInt(data.req_per_page));

        if (total_page < pagination_visible_pages) {
            render_table_rows(all_data, data.req_per_page, data.page_no, 'NO_ALL');
            for (let num = 2; num <= total_page; num++) {
                pager += `<a href="#" onclick=active_page(this,\"${all_data}\",${data.req_per_page})>${num}</a>`;
            }
        } else {
            render_table_rows(all_data, data.req_per_page, data.page_no, 'NO_ALL');
            for (let num = 2; num <= pagination_visible_pages; num++) {
                pager += `<a href="#" onclick=active_page(this,\"${all_data}\",${data.req_per_page})>${num}</a>`;
            }
            for (let num = pagination_visible_pages + 1; num <= total_page; num++) {
                pager += `<a href="#" style="display:none;" onclick=active_page(this,\"${all_data}\",${data.req_per_page})>${num}</a>`;
            }
        }
        pager += `<a href="#" id="next_link" onclick=active_page('next',\"${all_data}\",${data.req_per_page})>&raquo;</a>`;
        $(".pagination").append(pager);
    } else {
        render_table_rows(all_data, length, 1, 'ALL');
    }
}

let myarr_global;
let len_global;

function addAllRows() {
    var apiUrl = "http://localhost:8080/archery/test/studentList";
    fetch(apiUrl).then(response => {
        return response.json();
    }).then(users_data => {
        myarr_global = users_data;
        return users_data;
    }).then(users_data => {
        len_global = users_data.length;
        pagination(data, myarr_global, len_global);
    }).catch(err => {
        alert("Fail");
        console.log(err);
    });
}

// trigger when requests per page dropdown changes
function filter_requests(options) {
    const data = {"req_per_page": options, "page_no": 1};
    pagination(data, myarr_global, len_global);
}

function addReqPerPage() {
    var tbody = document.getElementById("req_per_page").getElementsByTagName('tbody')[0];
    var row = document.createElement("tr")
    var td = document.createElement("td")

    let options = ['10', '15', 'ALL'];
    let select_req = document.createElement("select");
    select_req.onchange = function () {
        filter_requests(options[select_req.selectedIndex]);
    };
    var option10 = document.createElement("option");
    option10.value = '10';
    option10.text = '10';
    var option15 = document.createElement("option");
    option15.value = '15';
    option15.text = '15';
    var optionAll = document.createElement("option");
    optionAll.value = 'ALL';
    optionAll.text = 'ALL';

    select_req.appendChild(option10);
    select_req.appendChild(option15);
    select_req.appendChild(optionAll);
    select_req[2].selected = true;

    td.appendChild(select_req);
    row.appendChild(td);
    tbody.appendChild(row);

}

addReqPerPage();
addAllRows();

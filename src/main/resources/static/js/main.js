$(document).ready(function () {
    showTabl();
    $('#searchButton').click(showTabl);

    function showTabl() {
        console.log($("#searchInput").val());

        $.ajax({
            type: 'GET',
            url: '/get-books',
            dataType: 'json',
            data: {
                getBy: $("#searchInput").val()
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Content-Type', 'application/json')
            },
            success: function (res) {
                console.log(res);
                $('#tableBook').html(searchResults(res));
            },
            error: function (response) {
                console.log(response);
            }
        })
    }


    function searchResults(res){
        let tabl = "<table class='table table-hover'>"+
            "<thead class='table-dark'>"+
            "<th>Isbn</th>"+
            "<th>Title</th>"+
            "<th>Author</th>"+
            "</thead>"+
            "<tbody>";

        for(let i=0; i<res.length; i++){
            tabl +=  "<tr>"+
                "<td>"+res[i].isbn+"</td>"+
                "<td>"+res[i].title+"</td>"+
                "<td>"+res[i].author+"</td>"+
                "</tr>";
        }

        tabl += "</tbody></table>";
        return tabl;
    }

    $('#addButton').click(function (event){
        event.preventDefault();
        let book = {
            isbn: $("#isbn").val(),
            title: $("#title").val(),
            author: $("#author").val()
        }
        console.log(book);

        $.ajax({
            type: 'POST',
            url: '/add-book',

            data: JSON.stringify(book),
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Content-Type', 'application/json')
            },
            success: function (response) {
                console.log(response);
                showTabl();
                $("#isbn").val("");
                $("#title").val("");
                $("#author").val("");
            },
            error: function (response) {
                alert(response.responseText);
                $("#isbn").val("");
                $("#title").val("");
                $("#author").val("");
                console.log(response.responseText);
            }
        })
    });
});
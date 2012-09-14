/**
 * Created with IntelliJ IDEA.
 * User: idzivinskyi
 * Date: 14.09.12
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */

$(document).ready(function () {
    selectTable();
});

function selectTable() {
    $.post(
        "/json/post",
        {title:"aaa", singer: "ssss"},

        function (response) {
            console.log(response);
        });
}

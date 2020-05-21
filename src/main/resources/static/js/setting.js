$(function () {
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        url: "http://upload-z2.qiniup.com", // 华南
        method: "post",
        processData: false, // 不要把表单的内容转字符串
        contentType: false, // 不让jQ设置参数类型，浏览器自动解析
        data: new FormData($("#uploadForm")[0]),
        success: function (data) {
            if (data && data.code == 0){
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code == 0){
                            window.location.reload();
                        }else {
                            alert(data.msg);
                        }
                    }
                );
            }else {
                alert("上传失败！");
            }

        }

    });
    return false;
}
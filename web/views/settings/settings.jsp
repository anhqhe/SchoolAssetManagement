<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Cài đặt hệ thống</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">

<div id="wrapper">

    <%@ include file="/views/layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="/views/layout/topbar.jsp" %>

            <div class="container-fluid">

                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800">
                        <i class="fas fa-cogs text-primary"></i> Cài đặt hệ thống
                    </h1>
                </div>

                <c:if test="${not empty param.success}">
                    <div class="alert alert-success">Đã lưu cài đặt.</div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <%-- Thông báo lỗi từ server (không nên chứa nội dung nhạy cảm như stacktrace) --%>
                    <div class="alert alert-danger">${errorMessage}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/settings" class="mb-4">
                    <%-- TODO bảo mật: nên bổ sung CSRF token cho form POST --%>

                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">Theme</h6>
                        </div>
                        <div class="card-body">
                            <div class="form-group">
                                <label for="uiPrimaryColor">Màu chủ đạo</label>
                                <div class="d-flex align-items-center" style="gap: 12px;">
                                    <input type="color" id="uiPrimaryColor" name="uiPrimaryColor"
                                           value="${uiPrimaryColor}" class="form-control p-0"
                                           style="width: 56px; height: 40px;">
                                    <input type="text" class="form-control" value="${uiPrimaryColor}" readonly>
                                </div>
                                <small class="form-text text-muted">Chỉ ảnh hưởng giao diện (màu nhấn, nút, icon).</small>
                            </div>
                        </div>
                    </div>

                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold text-primary">Banner toàn hệ thống</h6>
                        </div>
                        <div class="card-body">
                            <div class="form-group">
                                <div class="custom-control custom-switch">
                                    <input type="checkbox" class="custom-control-input" id="uiBannerEnabled" name="uiBannerEnabled"
                                           <c:if test="${uiBannerEnabled}">checked</c:if>>
                                    <label class="custom-control-label" for="uiBannerEnabled">Bật banner</label>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="uiBannerText">Nội dung banner</label>
                                <%-- Banner sẽ được render ở topbar; cần escape khi output để tránh XSS nếu nội dung chứa HTML --%>
                                <textarea id="uiBannerText" name="uiBannerText" class="form-control" rows="3"
                                          maxlength="300">${uiBannerText}</textarea>
                                <small class="form-text text-muted">Banner chỉ hiển thị khi bật.</small>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex justify-content-end">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Lưu cài đặt
                        </button>
                    </div>
                </form>

            </div>
        </div>

        <%@ include file="/views/layout/footer.jsp" %>

    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>

<script>
    (function () {
        const colorInput = document.getElementById('uiPrimaryColor');
        const colorText = colorInput ? colorInput.parentElement.querySelector('input[type="text"]') : null;
        if (colorInput && colorText) {
            colorInput.addEventListener('input', function () {
                colorText.value = colorInput.value;
            });
        }

        const bannerToggle = document.getElementById('uiBannerEnabled');
        const bannerText = document.getElementById('uiBannerText');
        function syncBannerDisabled() {
            if (!bannerToggle || !bannerText) return;
            bannerText.disabled = !bannerToggle.checked;
        }
        if (bannerToggle && bannerText) {
            bannerToggle.addEventListener('change', syncBannerDisabled);
            syncBannerDisabled();
        }
    })();
</script>

</body>
</html>


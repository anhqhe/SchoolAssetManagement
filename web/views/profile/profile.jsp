<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    User profileUser = (User) request.getAttribute("profileUser");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hồ sơ | School Asset Management</title>

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
                        <i class="fas fa-user text-primary"></i> Hồ sơ cá nhân
                    </h1>
                    <div>
                        <button type="button" class="btn btn-primary btn-sm" id="btnEdit">
                            <i class="fas fa-edit"></i> Chỉnh sửa
                        </button>
                        <button type="submit" form="profileForm" class="btn btn-success btn-sm d-none" id="btnSave">
                            <i class="fas fa-save"></i> Lưu
                        </button>
                        <a class="btn btn-secondary btn-sm d-none" id="btnCancel"
                           href="${pageContext.request.contextPath}/profile">
                            Hủy
                        </a>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle"></i> ${error}
                    </div>
                </c:if>

                <div class="row">
                    <div class="col-lg-8">
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">Thông tin tài khoản</h6>
                            </div>
                            <div class="card-body">
                                <form id="profileForm" method="post" action="${pageContext.request.contextPath}/profile">
                                    <div class="form-group">
                                        <label class="text-muted mb-1">Tên đăng nhập</label>
                                        <input type="text" class="form-control" value="<%= profileUser != null ? profileUser.getUsername() : "" %>" readonly>
                                    </div>

                                    <div class="form-group">
                                        <label class="text-muted mb-1">Họ và tên</label>
                                        <input type="text" name="fullName" class="form-control js-editable"
                                               value="<%= profileUser != null ? profileUser.getFullName() : "" %>"
                                               readonly required>
                                    </div>

                                    <div class="form-group">
                                        <label class="text-muted mb-1">Email</label>
                                        <input type="email" name="email" class="form-control js-editable"
                                               value="<%= profileUser != null ? (profileUser.getEmail() != null ? profileUser.getEmail() : "") : "" %>"
                                               readonly required>
                                    </div>

                                    <div class="form-group">
                                        <label class="text-muted mb-1">Số điện thoại</label>
                                        <input type="text" name="phone" class="form-control js-editable"
                                               value="<%= profileUser != null ? (profileUser.getPhone() != null ? profileUser.getPhone() : "") : "" %>"
                                               pattern="^(\+84|0)(\d[\d\s-]{7,})$"
                                               title="VD: 0912345678 hoặc +84912345678 (có thể nhập khoảng trắng/gạch ngang)"
                                               readonly required>
                                    </div>
                                </form>

                                <div class="row mb-3">
                                    <div class="col-sm-4 text-muted">Trạng thái</div>
                                    <div class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${profileUser != null && profileUser.active}">
                                                <span class="badge badge-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-secondary">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-4 text-muted">Vai trò</div>
                                    <div class="col-sm-8">
                                        <c:choose>
                                            <c:when test="${not empty profileUser.roles}">
                                                <c:forEach var="r" items="${profileUser.roles}" varStatus="st">
                                                    <span class="badge badge-info">${r}</span>
                                                    <c:if test="${!st.last}"> </c:if>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="card shadow mb-4">
                            <div class="card-body text-center">
                                <img class="img-profile rounded-circle mb-3"
                                     style="width: 96px; height: 96px;"
                                     src="${pageContext.request.contextPath}/assets/img/undraw_profile.svg" alt="avatar">
                                <div class="h6 mb-0 font-weight-bold text-gray-800">
                                    <%= profileUser != null ? profileUser.getFullName() : "" %>
                                </div>
                                <div class="small text-muted">
                                    <%= profileUser != null ? profileUser.getUsername() : "" %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>

        <%@ include file="/views/layout/footer.jsp" %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
<script>
    (function () {
        const editModeFromServer = <%= Boolean.TRUE.equals(request.getAttribute("editMode")) ? "true" : "false" %>;
        const hasError = <%= request.getAttribute("error") != null ? "true" : "false" %>;

        const btnEdit = document.getElementById('btnEdit');
        const btnSave = document.getElementById('btnSave');
        const btnCancel = document.getElementById('btnCancel');
        const editableInputs = document.querySelectorAll('.js-editable');

        function setEditMode(isEdit) {
            editableInputs.forEach(function (el) {
                el.readOnly = !isEdit;
            });
            if (btnEdit) btnEdit.classList.toggle('d-none', isEdit);
            if (btnSave) btnSave.classList.toggle('d-none', !isEdit);
            if (btnCancel) btnCancel.classList.toggle('d-none', !isEdit);
            if (isEdit && editableInputs.length > 0) {
                editableInputs[0].focus();
                editableInputs[0].setSelectionRange(editableInputs[0].value.length, editableInputs[0].value.length);
            }
        }

        if (btnEdit) {
            btnEdit.addEventListener('click', function () {
                setEditMode(true);
            });
        }

        setEditMode(editModeFromServer || hasError);
    })();
</script>

</body>
</html>


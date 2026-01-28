<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh Sách Yêu Cầu Chờ Xử Lý</title>
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
</head>
<body id="page-top">
<div id="wrapper">

    <%@ include file="../layout/sidebar.jsp" %>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">

            <%@ include file="../layout/topbar.jsp" %>

            <!-- Page Content -->
            <div class="container-fluid mt-4">
                <div class="card shadow">
                    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Phiếu Yêu Cầu Đang Chờ</h5>
                    </div>
                    <div class="card-body">

<%--                        <c:if test="${not empty msg}">--%>
<%--                            <div class="alert alert-success">${msg}</div>--%>
<%--                        </c:if>--%>

    <c:choose>
        <c:when test="${not empty param.msg}">
            <c:set var="alertClass" value="alert-info"/>
            <c:choose>
                <c:when test="${param.msg == 'success'}"><c:set var="alertClass" value="alert-success"/></c:when>
                <c:when test="${param.msg == 'out_of_stock'}"><c:set var="alertClass" value="alert-warning"/></c:when>
                <c:when test="${param.msg == 'error'}"><c:set var="alertClass" value="alert-danger"/></c:when>
            </c:choose>
            <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
                <c:choose>
                    <c:when test="${param.msg == 'success'}">Gửi BGH duyệt thành công.</c:when>
                    <c:when test="${param.msg == 'out_of_stock'}">Không đủ hàng trong kho — không thể gửi BGH.</c:when>
                    <c:when test="${param.msg == 'error'}">Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại sau.</c:when>
                    <c:otherwise>${fn:escapeXml(param.msg)}</c:otherwise>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:when>

    </c:choose>


                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-secondary">
                                <tr>
                                    <th>Mã Phiếu</th>
                                    <th>Người Gửi</th>
                                    <th>Ngày Tạo</th>
                                    <th>Phòng Yêu Cầu</th>
                                    <th>Mục Đích</th>
                                    <th>Trạng Thái</th>
                                    <th class="text-center">Thao Tác</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="req" items="${pendingRequests}">
                                    <tr>
                                        <td class="fw-bold text-primary">${req.requestCode}</td>
                                        <td>${req.teacherId}</td>
                                        <td> ${req.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                                        <td>${req.requestedRoomId}</td>
                                        <td>${req.purpose}</td>
                                        <td><span class="badge bg-light">${req.status}</span></td>
                                        <td class="text-center">
                                            <a href="request-detail?id=${req.requestId}" class="btn btn-sm btn-info text-white">
                                                Chi tiết & Kiểm tra kho
                                            </a>

                                            <form action="${pageContext.request.contextPath}/staff/pending-requests" method="post" class="d-inline">
                                                <input type="hidden" name="requestId" value="${req.requestId}">
                                                <button type="submit" class="btn btn-sm btn-success"
                                                        onclick="return confirm('Xác nhận đã kiểm tra kho và gửi lên Ban Giám Hiệu?')">
                                                    Gửi BGH duyệt
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <c:if test="${empty pendingRequests}">
                                    <tr>
                                        <td colspan="7" class="text-center text-muted">Không có yêu cầu nào đang chờ xử lý.</td>
                                    </tr>
                                </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <%@ include file="../layout/footer.jsp" %>

    </div>
</div>



<script>
    function addItem() {
        const itemList = document.getElementById('itemList');
        const firstRow = itemList.querySelector('.item-row');
        const newRow = firstRow.cloneNode(true);

        // Reset giá trị các input trong dòng mới
        newRow.querySelectorAll('input').forEach(input => input.value = (input.type === 'number' ? 1 : ''));
        newRow.querySelectorAll('select').forEach(select => select.selectedIndex = 0);

        itemList.appendChild(newRow);
    }
</script>

</body>
</html>
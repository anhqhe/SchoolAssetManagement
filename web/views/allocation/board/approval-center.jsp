<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trung Tâm Phê Duyệt - BGH</title>
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
            <div class="container mt-5">
                <div class="card shadow border-0">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Danh Sách Phiếu Chờ Duyệt</h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead class="table-light">
                            <tr>
                                <th>Loại Phiếu</th>
                                <th>Mã Tham Chiếu</th>
                                <th>Người Yêu Cầu</th>
                                <th>Lý Do/Mục Đích</th>
                                <th class="text-center">Thao Tác</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="app" items="${pendingApprovals}">
                                <tr>
<%--                                    <td>--%>
<%--                                <span class="badge ${app.refType == 'ASSET_REQUEST' ? 'bg-info' : 'bg-warning'}">--%>
<%--                                        ${app.refType}--%>
<%--                                </span>--%>
<%--                                    </td>--%>
                                    <td>${app.refType}</td>
                                    <td><strong>${app.refId}</strong></td>
<%--                                    <td>${app.requesterName}</td>--%>

                                    <td>ref from other table</td>
                                    <td>${app.decision}</td>
                                    <td class="text-center">
                                        <form action="/board/approve" method="post" class="d-inline">
                                            <input type="hidden" name="refId" value="${app.refId}">
                                            <input type="hidden" name="refType" value="${app.refType}">
                                            <input type="text" name="decisionNote" class="form-control-sm border-1" placeholder="Ghi chú...">

                                            <button type="submit" name="decision" value="APPROVED" class="btn btn-sm btn-success">Duyệt</button>
                                            <button type="submit" name="decision" value="REJECTED" class="btn btn-sm btn-danger">Từ Chối</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty pendingApprovals}">
                                <tr><td colspan="5" class="text-center">Không có phiếu nào cần duyệt.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
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
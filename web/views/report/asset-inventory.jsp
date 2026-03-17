<%-- 
    Document   : asset-inventory
    Created on : Mar 16, 2026, 10:54:29 AM
    Author     : An
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Báo cáo tài sản tồn kho</title>
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
                        <i class="fas fa-file-alt text-primary"></i> Asset Inventory Report
                    </h1>
                </div>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">Tổng hợp theo danh mục & trạng thái</h6>
                    </div>
                    <div class="card-body">
                        <table class="table table-bordered">
                            <thead>
                            <tr>
                                <th>Danh mục</th>
                                <th>Trạng thái</th>
                                <th>Tổng số tài sản</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="row" items="${inventoryData}">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/asset-report?type=inventoryDetail&categoryId=${row.categoryId}">
                                            ${row.categoryName}
                                        </a>
                                    </td>
                                    <td>${row.status}</td>
                                    <td class="text-right">${row.totalAssets}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty inventoryData}">
                                <tr>
                                    <td colspan="3" class="text-center">Không có dữ liệu</td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>

        <%@ include file="/views/layout/footer.jsp" %>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
</body>
</html>

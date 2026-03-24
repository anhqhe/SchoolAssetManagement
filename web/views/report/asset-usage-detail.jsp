<%-- 
    Document   : asset-usage-detail
    Created on : Mar 16, 2026, 2:22:23 PM
    Author     : An
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết sử dụng tài sản</title>
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
                                <i class="fas fa-file-alt text-primary"></i> Chi tiết sử dụng tài sản
                            </h1>
                            <a href="${pageContext.request.contextPath}/asset-report?type=usage"
                               class="btn btn-sm btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại tổng hợp
                            </a>
                        </div>

                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">
                                    Tài sản đang sử dụng (Phòng ID: ${roomId}, Category ID: ${categoryId})
                                </h6>
                            </div>
                            <div class="card-body">
                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>Mã tài sản</th>
                                            <th>Tên tài sản</th>
                                            <th>Số seri</th>
                                            <th>Model</th>
                                            <th>Hãng</th>
                                            <th>Trạng thái</th>
                                            <th>Cập nhật gần nhất</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="row" items="${detailData}">
                                        <tr>
                                            <td>${row.assetCode}</td>
                                            <td>${row.assetName}</td>
                                            <td>${row.serialNumber}</td>
                                            <td>${row.model}</td>
                                            <td>${row.brand}</td>
                                            <td>${row.status}</td>
                                            <td>
                                        <c:choose>
                                            <c:when test="${row.updatedAt != null}">
                                                ${row.updatedAt}
                                            </c:when>
                                            <c:otherwise>-</c:otherwise>
                                        </c:choose>
                                        </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty detailData}">
                                        <tr>
                                            <td colspan="7" class="text-center">Không có dữ liệu</td>
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

<%@ page contentType="text/html;charset=UTF-8" %>

<div class="container-fluid">

    <h1 class="h3 mb-2 text-gray-800">Quản lý tài sản</h1>
    <p class="mb-4">Danh sách tài sản của trường</p>

    <div class="card shadow mb-4">
        <div class="card-header py-3">
            <h6 class="m-0 font-weight-bold text-primary">Danh sách tài sản</h6>
        </div>

        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                    <thead>
                    <tr>
                        <th>Mã tài sản</th>
                        <th>Tên tài sản</th>
                        <th>Loại</th>
                        <th>Phòng</th>
                        <th>Tình trạng</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>TS001</td>
                        <td>Máy chiếu</td>
                        <td>Thiết bị</td>
                        <td>P101</td>
                        <td>Tốt</td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

</div>

<!-- DATATABLE JS -->
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/datatables/dataTables.bootstrap4.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/demo/datatables-demo.js"></script>

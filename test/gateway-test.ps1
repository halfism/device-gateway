# IoT Device Gateway - 功能测试脚本

echo "================================="
echo "IoT Device Gateway - 功能测试"
echo "================================="

echo "1. 检查应用是否正在运行..."
netstat -an | findstr :8080
if ($LASTEXITCODE -eq 0) {
    echo "✓ 端口8080正在监听"
} else {
    echo "✗ 端口8080未监听，请先启动应用"
    exit 1
}

echo ""
echo "2. 测试代理网关状态接口..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/proxy/status" -Method Get
    echo "✓ 代理网关状态接口正常: $response"
} catch {
    echo "✗ 代理网关状态接口异常: $_"
}

echo ""
echo "3. 测试代理网关连接接口..."
$deviceData = @{
    deviceId = "test-device-001"
    deviceName = "测试设备"
    protocol = "MQTT"
    host = "localhost"
    port = 1883
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/proxy/connect" -Method Post -Body $deviceData -ContentType "application/json"
    echo "✓ 代理网关连接接口正常: $response"
} catch {
    echo "✗ 代理网关连接接口异常: $_"
}

echo ""
echo "4. 测试南向网关设备列表接口..."
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/southbound/devices" -Method Get
    echo "✓ 南向网关设备列表接口正常: $response"
} catch {
    echo "✗ 南向网关设备列表接口异常: $_"
}

echo ""
echo "================================="
echo "测试完成!"
echo "================================="
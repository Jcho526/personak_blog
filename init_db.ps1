# 数据库初始化脚本
# 请确保您已安装 MySQL 并将其添加到了系统环境变量 PATH 中

$ErrorActionPreference = "Stop"

function Test-CommandExists {
    param ($command)
    $oldPreference = $ErrorActionPreference
    $ErrorActionPreference = "SilentlyContinue"
    $path = Get-Command $command
    $ErrorActionPreference = $oldPreference
    return $path -ne $null
}

if (-not (Test-CommandExists "mysql")) {
    Write-Host "错误: 未找到 'mysql' 命令。请确保安装了 MySQL 并将 bin 目录添加到系统 PATH 中。" -ForegroundColor Red
    exit 1
}

$InitSqlPath = ".\blog-backend\src\main\resources\db\init.sql"

if (-not (Test-Path $InitSqlPath)) {
    Write-Host "错误: 未找到初始化脚本: $InitSqlPath" -ForegroundColor Red
    exit 1
}

Write-Host "正在准备初始化数据库..." -ForegroundColor Cyan
Write-Host "脚本路径: $InitSqlPath" -ForegroundColor Gray

$User = Read-Host "请输入 MySQL 用户名 (默认: root)"
if ([string]::IsNullOrWhiteSpace($User)) {
    $User = "root"
}

Write-Host "接下来请输入 MySQL 用户 '$User' 的密码..." -ForegroundColor Yellow

# 使用 cmd /c 来调用 mysql，以避免 PowerShell 的重定向编码问题
# 注意：这需要用户交互输入密码
$Command = "mysql -u $User -p < ""$InitSqlPath"""
cmd /c $Command

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n数据库初始化成功！" -ForegroundColor Green
    Write-Host "请记得检查 'blog-backend\src\main\resources\application-dev.yml' 中的数据库配置是否与您刚才使用的用户名密码一致。" -ForegroundColor Yellow
} else {
    Write-Host "`n数据库初始化失败，请检查错误信息。" -ForegroundColor Red
}

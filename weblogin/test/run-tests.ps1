$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$classesDir = Join-Path $projectRoot "build\test-classes"
$sourceList = Join-Path $projectRoot "build\test-sources.txt"
$compileLog = Join-Path $projectRoot "build\test-javac.log"

if (Test-Path $classesDir) {
    Remove-Item -LiteralPath $classesDir -Recurse -Force
}
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

Get-ChildItem -Path (Join-Path $projectRoot "src"), (Join-Path $projectRoot "test") -Recurse -Filter "*.java" |
    Where-Object { $_.FullName -notmatch "\\src\\Admin\\servlet\\AdminLoginServlet\.java$" } |
    ForEach-Object { $_.FullName } |
    ForEach-Object -Begin { $sources = New-Object System.Collections.Generic.List[string] } -Process { $sources.Add($_) } -End {
        [System.IO.File]::WriteAllLines($sourceList, $sources, (New-Object System.Text.UTF8Encoding($false)))
    }

$classpath = @(
    Join-Path $projectRoot "WEB-INF\lib\servlet-api.jar"
) -join ";"

Push-Location $projectRoot
try {
    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    javac -encoding UTF-8 -cp $classpath -d $classesDir "@$sourceList" 2> $compileLog
    $javacExitCode = $LASTEXITCODE
    $ErrorActionPreference = $previousErrorActionPreference
    if ($javacExitCode -ne 0) {
        Get-Content -LiteralPath $compileLog
        throw "javac failed with exit code $javacExitCode"
    }
    Remove-Item -LiteralPath $compileLog -Force -ErrorAction SilentlyContinue
    java -cp "$classesDir;$classpath" FullSystemTestRunner
    if ($LASTEXITCODE -ne 0) {
        throw "FullSystemTestRunner failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
    Remove-Item -LiteralPath $classesDir -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath (Join-Path $projectRoot "build\test-data") -Recurse -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $sourceList -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $compileLog -Force -ErrorAction SilentlyContinue
}

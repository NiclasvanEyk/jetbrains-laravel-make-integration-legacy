<?php

/**
 * This file was generated by the Laravel Make extension for Jetbrains IDEs.
 *
 * LARAVEL_MAKE_IDE_CONNECTOR_VERSION: 1.0.0
 */


class LaravelMakeIdeConnectorServiceProvider extends \Illuminate\Support\ServiceProvider
{
    public function boot()
    {
        if (!function_exists('socket_create')) {
            return;
        }

        if (!$this->app->runningInConsole()) {
            return;
        }

        $socket = socket_create(AF_UNIX, SOCK_STREAM, 0);
        if (!$socket) return;

        $connected = socket_connect($socket, __DIR__ . '/ide_connector.socket');
        if (!$connected) return;

        \Event::listen('*', function ($eventName) use ($socket) {
            socket_write($socket, $eventName . "\n");
        });
    }
}